package com.talan.adminmodule.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {
    private String name;
    private String type;
    @NonNull
    private ColumnInfo pk;
    private Long totalRows;
    private List<ColumnInfo> columns;
    private List<String> foreignKeys;
}
