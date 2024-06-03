package com.talan.adminmodule.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micrometer.common.lang.Nullable;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteRequest {
    private String tableName;
    private String primaryKeyValue;
    @Nullable
    private String ussername;
    @Nullable
    private List<String> occurences;


    public DeleteRequest(String tableName, String primaryKeyValue, String username) {
        this.tableName=tableName;
        this.primaryKeyValue=primaryKeyValue;
        this.ussername=username;
    }
    public DeleteRequest(String tableName, String primaryKeyValue) {
        this.tableName=tableName;
        this.primaryKeyValue=primaryKeyValue;

    }
    public DeleteRequest(String tableName, List<String> occurences) {
        this.tableName=tableName;
        this.occurences=occurences;
    }
}
