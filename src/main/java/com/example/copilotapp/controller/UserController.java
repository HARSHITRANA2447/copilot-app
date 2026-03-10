package com.example.copilotapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.copilotapp.service.UserService;
import com.example.copilotapp.model.User;

import java.util.Collection;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users
    @GetMapping
    public Collection<User> listUsers() {
        return userService.listUsers();
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User payload) {
        // simple validation
        if (isBlank(payload.getName()) || isBlank(payload.getEmail())) {
            return ResponseEntity.badRequest().body("name and email are required");
        }
        User user = userService.createUser(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User payload) {
        if (isBlank(payload.getName()) || isBlank(payload.getEmail())) {
            return ResponseEntity.badRequest().body("name and email are required");
        }
        User updated = userService.updateUser(id, payload);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        User removed = userService.deleteUser(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // Helper
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
