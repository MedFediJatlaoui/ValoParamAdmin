package com.talan.adminmodule.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInfo {
    @NonNull
    private String name;
    private String type;
    private String isNullable;
    private String size;
    private String isAutoIncrement;
}
