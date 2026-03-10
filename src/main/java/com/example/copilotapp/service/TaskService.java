package com.example.copilotapp.service;

import com.example.copilotapp.model.Task;
import com.example.copilotapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Return all tasks
     */
    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    /**
     * Get a task by id, or null if not found
     */
    public Task getTask(Long id) {
        Optional<Task> t = taskRepository.findById(id);
        return t.orElse(null);
    }

    /**
     * Create and persist a new task
     */
    public Task createTask(Task payload) {
        // timestamps are handled by entity callbacks (@PrePersist)
        Task task = new Task();
        task.setTitle(payload.getTitle());
        task.setDescription(payload.getDescription());
        task.setCompleted(payload.isCompleted());
        return taskRepository.save(task);
    }

    /**
     * Update an existing task, or return null if not found
     */
    public Task updateTask(Long id, Task payload) {
        Optional<Task> existingOpt = taskRepository.findById(id);
        if (existingOpt.isEmpty()) return null;
        Task existing = existingOpt.get();
        if (payload.getTitle() != null) existing.setTitle(payload.getTitle());
        if (payload.getDescription() != null) existing.setDescription(payload.getDescription());
        existing.setCompleted(payload.isCompleted());
        // updatedAt is handled by @PreUpdate
        return taskRepository.save(existing);
    }

    /**
     * Delete a task and return the deleted entity, or null if not found
     */
    public Task deleteTask(Long id) {
        Optional<Task> existingOpt = taskRepository.findById(id);
        if (existingOpt.isEmpty()) return null;
        Task existing = existingOpt.get();
        taskRepository.deleteById(id);
        return existing;
    }

    /**
     * Mark a task as completed and return the updated entity, or null if not found
     */
    public Task markComplete(Long id) {
        Optional<Task> existingOpt = taskRepository.findById(id);
        if (existingOpt.isEmpty()) return null;
        Task existing = existingOpt.get();
        existing.setCompleted(true);
        return taskRepository.save(existing);
    }

    // Query helpers that delegate to repository
    public List<Task> findByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }

    public List<Task> searchByTitle(String q) {
        return taskRepository.findByTitleContainingIgnoreCase(q == null ? "" : q);
    }

    public List<Task> findByCreatedBetween(Instant start, Instant end) {
        return taskRepository.findByCreatedAtBetween(start, end);
    }

    public List<Task> findAllOrderedByCreatedAtDesc() {
        return taskRepository.findAllByOrderByCreatedAtDesc();
    }
}
