package com.talan.adminmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UpdateRequest {
    Map<String, String> instanceData;
    String tableName;
    String username;
}
