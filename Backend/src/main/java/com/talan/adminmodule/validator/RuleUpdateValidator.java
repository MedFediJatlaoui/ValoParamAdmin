package com.talan.adminmodule.validator;

import com.talan.adminmodule.dto.RuleUpdateDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RuleUpdateValidator {
    private RuleUpdateValidator(){

    }
    public static List<String> validate(RuleUpdateDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Fill rule name ");
            errors.add("Fill rule description ");
            errors.add("Select rule category ");
            errors.add("Select at least one attribute");
            return errors;
        }

        if (!StringUtils.hasLength(dto.getName())) {
            errors.add("Fill rule name");
        }
        if (!StringUtils.hasLength(dto.getDescription())) {
            errors.add("Fill rule description ");
        }
        if (dto.getAttributeDtos() == null) {
            errors.add("Select at least one attribute");
        }
        if (dto.getCategory() == null) {
            errors.add("Select rule category");
        }
        return errors;
    }

}