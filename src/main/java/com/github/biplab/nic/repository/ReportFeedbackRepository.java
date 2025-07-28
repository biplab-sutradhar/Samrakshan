package com.github.biplab.nic.repository;

import com.github.biplab.nic.entity.ReportFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportFeedbackRepository extends JpaRepository<ReportFeedback, Long> {

    // Get all feedback for a specific report
    List<ReportFeedback> findByReportIdOrderByCreatedAtDesc(Long reportId);

    // Get pending feedback for a person (reports they need to address)
    List<ReportFeedback> findByFeedbackToAndStatus(UUID personId, String status);

    // Get all feedback given by a supervisor
    List<ReportFeedback> findByFeedbackFromOrderByCreatedAtDesc(UUID supervisorId);

    // Check if report has pending feedback
    boolean existsByReportIdAndStatus(Long reportId, String status);
}
