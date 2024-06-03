package com.talan.adminmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParamAudit {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column
    private String tableName;
    private String action;
    private Integer version;
    private String rowId;
    @Column(columnDefinition = "TEXT")
    private String newRow;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy ;
    private LocalDateTime lastModifiedAt;

    public ParamAudit(String tableName, String action, Integer version, String newRow,String createdBy, LocalDateTime createdAt) {
        this.tableName=tableName;
        this.action=action;
        this.version=version;
        this.newRow=newRow;
        this.createdBy=createdBy;
        this.createdAt=createdAt;
    }
    public ParamAudit(String tableName,String rowId, String action, Integer version,String lastModifiedBy, LocalDateTime lastModifiedAt) {
        this.tableName=tableName;
        this.rowId=rowId;
        this.action=action;
        this.version=version;
        this.lastModifiedBy=lastModifiedBy;
        this.lastModifiedAt=lastModifiedAt;
    }
    public ParamAudit(String tableName,String rowId,String newRow,String action, Integer version,String lastModifiedBy, LocalDateTime lastModifiedAt) {
        this.tableName=tableName;
        this.rowId=rowId;
        this.newRow=newRow;
        this.action=action;
        this.version=version;
        this.lastModifiedBy=lastModifiedBy;
        this.lastModifiedAt=lastModifiedAt;
    }



    public static ParamAudit constructForInsertion(String tableName, String action, Integer version, String newRow,String createdBy) {
        LocalDateTime createdAt = LocalDateTime.now();
        return new ParamAudit(tableName, action, version, newRow,createdBy, createdAt);
    }
    public static ParamAudit constructForDeletion(String tableName,String rowId, String action, Integer version,String lastmodifiedBy) {
        LocalDateTime lastmodifiedAt = LocalDateTime.now();
        return new ParamAudit(tableName,rowId, action, version,lastmodifiedBy, lastmodifiedAt);
    }
    public static ParamAudit constructForUpdate(String tableName,String rowId,String newRow, String action, Integer version,String lastModifiedBy) {
        LocalDateTime lastmodifiedAt = LocalDateTime.now();
        return new ParamAudit(tableName,rowId,newRow, action, version,lastModifiedBy, lastmodifiedAt);
    }

}
