package com.tadalatestudio.dbrun;

import com.tadalatestudio.model.Role;
import com.tadalatestudio.model.User;
import com.tadalatestudio.repository.UserRepository;
import com.tadalatestudio.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing data...");

        roleService.initDefaultRoles();

        //create admin if not exist
        if (!userRepository.existsByEmail("atalibdev@google.com")) {
            Role admin = roleService.getRoleByName(Role.ROLE_ADMIN);
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(admin);

            User adminUser = User.builder()
                    .email("atalibdev@google.com")
                    .password(passwordEncoder.encode("JavaDev12#"))
                    .firstName("Almousleck")
                    .lastName("Atalib")
                    .roles(adminRoles)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();

            userRepository.save(adminUser);
            log.info("Created default admin user: admin@example.com");
        }

        log.info("Data initialization completed");
    }
}
