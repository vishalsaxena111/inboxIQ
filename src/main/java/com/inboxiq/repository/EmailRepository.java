package com.inboxiq.repository;

import com.inboxiq.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    List<Email> findByCategory(String category);
    List<Email> findAllByOrderByReceivedAtDesc();
    List<Email> findByCategoryIsNotNull();
}
