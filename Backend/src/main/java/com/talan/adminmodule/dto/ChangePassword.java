package com.talan.adminmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ChangePassword {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
    private String message;
}
