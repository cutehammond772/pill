package me.cutehammond.pill.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponseHeader {

    private int code;
    private String message;

}
