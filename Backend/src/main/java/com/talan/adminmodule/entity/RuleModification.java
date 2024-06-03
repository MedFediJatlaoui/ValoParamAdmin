package com.talan.adminmodule.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rule_modification")
public class RuleModification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "modification_description")
    private String modificationDescription;

    @Column(name = "modification_type")
    private String modificationType;

    @Column(name = "profile_image_path")
    private String profileImagePath ;


}
