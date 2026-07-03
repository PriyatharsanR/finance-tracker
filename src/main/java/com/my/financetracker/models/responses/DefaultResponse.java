package com.my.financetracker.models.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultResponse<T>{

    private String code;

    private String title;

    private String message;

    private T data;
}
