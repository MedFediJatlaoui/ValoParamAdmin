package com.talan.adminmodule.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchRequest {
    private String tableName;
    private List<String> columns;
    private String searchText;
}
