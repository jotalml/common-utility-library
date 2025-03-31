package com.jotalml.commonutilitylibrary.enums;

import lombok.Getter;

@Getter
public enum CompareEnum {

    EQUAL("equal"),
    IN_NUMBER("in"),
    IN_STRING("in"),
    START_WITH("start-with"),
    CONTAIN("contain"),
    NO_CONTAIN("no-contain"),
    LESS_THAN("less-than"),
    LESS_THAN_EQUAL("less-than-equal"),
    GREATER_THAN_EQUAL("greater-than-equal"),
    GREATER_THAN("greater-than"),
    NOT_EQUAL("not-equal"),
    EXIST("exist"),
    JSONB_ARRAY("jsonb array"),
    EQUAL_DECIMAL(""),
    IN_DECIMAL("");
    private final String value;

    CompareEnum(String value) {
        this.value = value;
    }
}
