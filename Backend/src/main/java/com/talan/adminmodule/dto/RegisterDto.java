package com.talan.adminmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.talan.adminmodule.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterDto {
    private String firstname;
    private String lastname;
    private String password ;
    private String email;
    private String company ;
    private String phone;
    private Role role;
}
