package com.talan.adminmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ForeignKey {
   String fkTableName;
    String fkColumnName;
    String referencedTable;
    String referencedColumn;
}
