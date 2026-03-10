package com.example.copilotapp.repository;

import com.example.copilotapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompleted(boolean completed);

    List<Task> findByTitleContainingIgnoreCase(String titlePart);

    List<Task> findByCreatedAtBetween(Instant start, Instant end);

    List<Task> findAllByOrderByCreatedAtDesc();
}
