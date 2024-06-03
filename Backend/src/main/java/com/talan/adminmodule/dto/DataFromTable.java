package com.talan.adminmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataFromTable {
    List<Map<String, Object>> data;
List<String> deleteRequests;
List<String> updateRequests;
}
