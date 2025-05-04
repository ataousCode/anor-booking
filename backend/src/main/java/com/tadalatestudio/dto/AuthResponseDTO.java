package com.tadalatestudio.dto;

import com.tadalatestudio.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String email;
    private List<Role> roles;

    public AuthResponseDTO(String accessToken, String refreshToken, Long userId, String email, Set<String> roleNames) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.roles = convertToRoleList(roleNames);
    }

    private List<Role> convertToRoleList(Set<String> roleNames) {
        List<Role> roleList = new ArrayList<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                Role role = new Role();
                role.setName(roleName);
                roleList.add(role);
            }
        }
        return roleList;
    }
}
