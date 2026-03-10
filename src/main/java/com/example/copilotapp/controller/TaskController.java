package com.example.copilotapp.controller;

import com.example.copilotapp.model.Task;
import com.example.copilotapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // GET /api/tasks?completed=&q=
    @GetMapping
    public List<Task> listTasks(@RequestParam(value = "completed", required = false) Boolean completed,
                                @RequestParam(value = "q", required = false) String q) {
        if (completed != null) {
            return taskService.findByCompleted(completed);
        }
        if (q != null && !q.trim().isEmpty()) {
            return taskService.searchByTitle(q.trim());
        }
        return taskService.listTasks();
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") Long id) {
        Task t = taskService.getTask(id);
        if (t == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(t);
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task payload) {
        if (payload == null || payload.getTitle() == null || payload.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("title is required");
        }
        Task created = taskService.createTask(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable("id") Long id, @RequestBody Task payload) {
        if (payload == null) {
            return ResponseEntity.badRequest().body("payload is required");
        }
        Task updated = taskService.updateTask(id, payload);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        Task removed = taskService.deleteTask(id);
        if (removed == null) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // POST /api/tasks/{id}/complete
    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> markComplete(@PathVariable("id") Long id) {
        Task updated = taskService.markComplete(id);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    // Optional: GET /api/tasks/created?start=...&end=...
    @GetMapping("/created")
    public List<Task> findByCreatedBetween(@RequestParam("start") String startIso,
                                           @RequestParam("end") String endIso) {
        Instant start = Instant.parse(startIso);
        Instant end = Instant.parse(endIso);
        return taskService.findByCreatedBetween(start, end);
    }
}
