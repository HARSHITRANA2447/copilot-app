package com.example.copilotapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.copilotapp.model.User;
import com.example.copilotapp.repository.UserRepository;

import java.time.Instant;

@Component
@Profile("dev")
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User(null, "Alice", "alice@example.com", Instant.now()));
            userRepository.save(new User(null, "Bob", "bob@example.com", Instant.now()));
        }
    }
}
