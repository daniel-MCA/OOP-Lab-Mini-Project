package com.qbank.repository;

import com.qbank.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectIgnoreCaseAndActiveTrue(String subject);
    List<Question> findBySubjectIgnoreCaseAndTopicIgnoreCaseAndActiveTrue(String subject, String topic);
    List<Question> findByAnomalyFlagTrue();
    List<Question> findBySubjectIgnoreCase(String subject);
}
