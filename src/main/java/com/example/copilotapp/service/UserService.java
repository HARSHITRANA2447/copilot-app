package com.example.copilotapp.service;

import org.springframework.stereotype.Service;
import com.example.copilotapp.model.User;
import com.example.copilotapp.repository.UserRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Collection<User> listUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        Optional<User> u = userRepository.findById(id);
        return u.orElse(null);
    }

    public User createUser(User payload) {
        User user = new User();
        user.setName(payload.getName());
        user.setEmail(payload.getEmail());
        user.setCreatedAt(Instant.now());
        return userRepository.save(user);
    }

    public User updateUser(Long id, User payload) {
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) return null;
        User existing = existingOpt.get();
        existing.setName(payload.getName());
        existing.setEmail(payload.getEmail());
        return userRepository.save(existing);
    }

    public User deleteUser(Long id) {
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) return null;
        User existing = existingOpt.get();
        userRepository.deleteById(id);
        return existing;
    }
}
