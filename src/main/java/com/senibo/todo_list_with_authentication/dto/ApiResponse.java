package com.senibo.todo_list_with_authentication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<Data>(
        Boolean success,
        String message,
        Data data,
        String error,
        List<String> errors
) {

    public static <Data> ApiResponse<Data> success(String message, Data data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, null, error, null);
    }
    public static <T> ApiResponse<T> error(String error, List<String> errors) {
        return new ApiResponse<>(false, null, null, error, errors);
    }
    public static <T> ApiResponse<T> validationErrors(List<String> errors) {
        return new ApiResponse<>(false, null, null, "Validation failed", errors);
    }
}
