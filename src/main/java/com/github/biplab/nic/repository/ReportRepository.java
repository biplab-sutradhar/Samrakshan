package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Basic queries
    List<Report> findByCaseIdAndIsFinalReportFalse(UUID caseId);
    Optional<Report> findByCaseIdAndIsFinalReportTrue(UUID caseId);
    List<Report> findByCaseId(UUID caseId);
    List<Report> findByPersonId(UUID personId);
    boolean existsByCaseIdAndPersonIdAndIsFinalReportFalse(UUID caseId, UUID personId);
    boolean existsByCaseIdAndDepartmentAndIsFinalReportFalse(UUID caseId, String department);

    // 🆕 UPDATED SEARCH QUERIES - Using CaseDetails

    // Search by Boy's Subdivision
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.boySubdivision = :subdivision AND r.isFinalReport = false")
    List<Report> findByBoySubdivision(@Param("subdivision") String subdivision);

    // Search by Girl's Subdivision
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.girlSubdivision = :subdivision AND r.isFinalReport = false")
    List<Report> findByGirlSubdivision(@Param("subdivision") String subdivision);

    // Search by Marriage Address (like district/area)
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.marriageAddress LIKE %:address% AND r.isFinalReport = false")
    List<Report> findByMarriageAddress(@Param("address") String address);

    // Search by Police Station
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.policeStationNearMarriageLocation = :policeStation AND r.isFinalReport = false")
    List<Report> findByPoliceStation(@Param("policeStation") String policeStation);

    // Search by Year (PostgreSQL compatible)
    @Query("SELECT r FROM Report r WHERE EXTRACT(YEAR FROM r.submittedAt) = :year AND r.isFinalReport = false")
    List<Report> findByYear(@Param("year") int year);

    // Search by Month and Year
    @Query("SELECT r FROM Report r WHERE EXTRACT(YEAR FROM r.submittedAt) = :year " +
            "AND EXTRACT(MONTH FROM r.submittedAt) = :month AND r.isFinalReport = false")
    List<Report> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Search by Date Range (for daily/weekly)
    @Query("SELECT r FROM Report r WHERE r.submittedAt BETWEEN :startDate AND :endDate " +
            "AND r.isFinalReport = false")
    List<Report> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // Search by specific day
    @Query("SELECT r FROM Report r WHERE DATE(r.submittedAt) = DATE(:date) AND r.isFinalReport = false")
    List<Report> findByDay(@Param("date") LocalDateTime date);

    // 🆕 UPDATED Combined search with CaseDetails filters
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE (:boySubdivision IS NULL OR cd.boySubdivision = :boySubdivision) " +
            "AND (:girlSubdivision IS NULL OR cd.girlSubdivision = :girlSubdivision) " +
            "AND (:marriageAddress IS NULL OR cd.marriageAddress LIKE %:marriageAddress%) " +
            "AND (:policeStation IS NULL OR cd.policeStationNearMarriageLocation = :policeStation) " +
            "AND (:year IS NULL OR EXTRACT(YEAR FROM r.submittedAt) = :year) " +
            "AND (:month IS NULL OR EXTRACT(MONTH FROM r.submittedAt) = :month) " +
            "AND r.isFinalReport = false")
    List<Report> findByMultipleFilters(@Param("boySubdivision") String boySubdivision,
                                       @Param("girlSubdivision") String girlSubdivision,
                                       @Param("marriageAddress") String marriageAddress,
                                       @Param("policeStation") String policeStation,
                                       @Param("year") Integer year,
                                       @Param("month") Integer month);

    // 🆕 Monthly summary queries with CaseDetails filters
    @Query("SELECT EXTRACT(MONTH FROM r.submittedAt) as month, COUNT(r) as count " +
            "FROM Report r " +
            "WHERE EXTRACT(YEAR FROM r.submittedAt) = :year AND r.isFinalReport = false " +
            "GROUP BY EXTRACT(MONTH FROM r.submittedAt) " +
            "ORDER BY EXTRACT(MONTH FROM r.submittedAt)")
    List<Object[]> getMonthlyReportCountsByYear(@Param("year") int year);

    @Query("SELECT EXTRACT(MONTH FROM r.submittedAt) as month, COUNT(r) as count " +
            "FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE EXTRACT(YEAR FROM r.submittedAt) = :year " +
            "AND (:boySubdivision IS NULL OR cd.boySubdivision = :boySubdivision) " +
            "AND (:girlSubdivision IS NULL OR cd.girlSubdivision = :girlSubdivision) " +
            "AND (:marriageAddress IS NULL OR cd.marriageAddress LIKE %:marriageAddress%) " +
            "AND r.isFinalReport = false " +
            "GROUP BY EXTRACT(MONTH FROM r.submittedAt) " +
            "ORDER BY EXTRACT(MONTH FROM r.submittedAt)")
    List<Object[]> getMonthlyReportCountsByYearWithFilters(@Param("year") int year,
                                                           @Param("boySubdivision") String boySubdivision,
                                                           @Param("girlSubdivision") String girlSubdivision,
                                                           @Param("marriageAddress") String marriageAddress);

    // 🆕 Additional useful queries with case details
    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.teamId = :teamId AND r.isFinalReport = false")
    List<Report> findByTeamId(@Param("teamId") UUID teamId);

    @Query("SELECT r FROM Report r JOIN ChildMarriageCase c ON r.caseId = c.id " +
            "JOIN CaseDetails cd ON cd.caseId = c " +
            "WHERE cd.supervisorId = :supervisorId AND r.isFinalReport = false")
    List<Report> findBySupervisorId(@Param("supervisorId") UUID supervisorId);
}
