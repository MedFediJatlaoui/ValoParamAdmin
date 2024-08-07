package com.talan.adminmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.talan.adminmodule.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UserDto {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean active ;
    private boolean nonExpired ;
    private String profileImagePath;
    private String phone;
    private String company ;
    private Role role;
    private List<String> authorities;
    private String error;
}


