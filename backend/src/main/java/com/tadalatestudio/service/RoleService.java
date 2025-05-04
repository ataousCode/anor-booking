package com.tadalatestudio.service;

import com.tadalatestudio.exception.ResourceNotFoundException;
import com.tadalatestudio.model.Role;
import com.tadalatestudio.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    /**
     * Get a role by name
     *
     * @param name the role name
     * @return the role
     * @throws ResourceNotFoundException if the role is not found
     */
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    /**
     * Get all roles
     *
     * @return list of all roles
     */
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Create a new role if it doesn't exist
     *
     * @param name the role name
     * @param description the role description
     * @return the created or existing role
     */
    @Transactional
    public Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name(name)
                            .description(description)
                            .build();
                    Role savedRole = roleRepository.save(role);
                    log.info("Created new role: {}", name);
                    return savedRole;
                });
    }

    /**
     * Initialize default roles
     */
    @Transactional
    public void initDefaultRoles() {
        createRoleIfNotExists(Role.ROLE_ADMIN, "Administrator with full access");
        createRoleIfNotExists(Role.ROLE_ORGANIZER, "Event organizer with event management access");
        createRoleIfNotExists(Role.ROLE_USER, "Regular user with basic access");
        log.info("Default roles initialized");
    }
}
