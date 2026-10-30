package com.qbank.repository;

import com.qbank.model.TestAttempt;
import com.qbank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByUserOrderByStartedAtDesc(User user);
}
