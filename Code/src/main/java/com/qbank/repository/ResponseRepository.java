package com.qbank.repository;

import com.qbank.model.Response;
import com.qbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByUserOrderByAttemptedAtDesc(User user);
    List<Response> findByQuestionId(Long questionId);
    List<Response> findByUser(User user);
}
