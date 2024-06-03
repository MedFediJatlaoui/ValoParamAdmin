package com.talan.adminmodule.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TableDataRequest {
    private List<String> columns;
    private String search;
    private String sortByColumn;
    private String sortOrder;
    private int limit;
    private int offset;

}
