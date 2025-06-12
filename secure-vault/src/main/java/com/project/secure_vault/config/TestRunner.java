// package com.project.secure_vault.config;

// import com.project.secure_vault.model.User;
// import com.project.secure_vault.repository.UserRepository;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Profile;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Component;

// @Component
// @Profile("!prod")  // only run in non‐production
// public class TestRunner {

//   @Bean
//   public CommandLineRunner testUserRepo(UserRepository userRepo) {
//     return args -> {
//       String testUser = "alice";
//       User existing = userRepo.findByUsername(testUser);
//       if (existing == null) {
//         User u = new User();
//         u.setUsername(testUser);
//         u.setPasswordHash(new BCryptPasswordEncoder().encode("password"));
//         userRepo.save(u);
//         System.out.println("✓ Created test user 'alice'");
//       } else {
//         System.out.println("✓ Found existing user 'alice'");
//       }
//       // Fetch back to verify
//       User fetched = userRepo.findByUsername(testUser);
//       System.out.println("Fetched user: " + fetched.getUsername());
//     };
//   }
// }
