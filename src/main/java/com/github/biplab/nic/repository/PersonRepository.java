package com.github.biplab.nic.repository;
import com.github.biplab.nic.entity.Person;
import com.github.biplab.nic.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByEmail(String email);


    List<Person> findByRoleAndSubdivision(Role role, String subdivision);

    List<Person> findByDepartmentAndSubdivisionAndRoleAndRankGreaterThanEqual(String department, String subdivision, Role role, Integer rank);

    List<Person> findByDepartmentAndSubdivisionAndRoleAndRank(String department, String subdivision, Role role, Integer rank);
    List<Person> findBySubdivision(String subdivision);

    @Query("SELECT p FROM Person p WHERE p.department = :department AND p.district = :district AND p.role = :role AND p.rank <= :rank")
    List<Person> findByDepartmentAndDistrictAndRoleAndRank(
            @Param("department") String department,
            @Param("district") String district,
            @Param("role") Role role,
            @Param("rank") Integer rank
    );

    List<Person> findByDepartmentAndSubdivisionAndRoleAndRankLessThanEqual(String department, String subdivision, Role role, int rank);
    List<Person> findByRoleAndDistrict(Role role, String district);
    List<Person> findByRoleAndDistrictAndSubdivision(Role role, String district, String subdivision);

    List<Person> findByDepartmentAndDistrictAndSubdivisionAndRoleAndRank(String string, String district, String subdivision, Role role, int i);

    List<Person> findByRoleAndSubdivisionAndRank(Role role, String subdivision, int i);

    List<String> findDistinctDepartmentsBySubdivision(String subdivision);

    List<Person> findByDepartmentAndDistrictAndRoleAndRankGreaterThanEqual(String deptName, String district, Role role, int i);

    @Query("SELECT p FROM Person p " +
            "WHERE (:role IS NULL OR p.role = :role) " +
            "AND (:department IS NULL OR p.department = :department) " +
            "AND (:rank IS NULL OR p.rank = :rank) " +
            "AND (:district IS NULL OR p.district = :district) " +
            "AND (:designation IS NULL OR p.designation = :designation) " +
            "AND (:officeName IS NULL OR p.officeName = :officeName) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:subdivision IS NULL OR p.subdivision = :subdivision) " +
            "AND (:postName IS NULL OR p.postName = :postName)")
    List<Person> findByFilters(
            @Param("role") String role,
            @Param("department") String department,
            @Param("rank") Integer rank,
            @Param("district") String district,
            @Param("designation") String designation,
            @Param("officeName") String officeName,
            @Param("status") String status,
            @Param("subdivision") String subdivision,
            @Param("postName") String postName
    );


}
