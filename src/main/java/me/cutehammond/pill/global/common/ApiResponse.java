package me.cutehammond.pill.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class ApiResponse<T> {

    private static final int SUCCESS = 200;
    private static final int NOT_FOUND = 400;
    private static final int FAILED = 500;

    private static final String SUCCESS_MESSAGE = "SUCCESS";
    private static final String NOT_FOUND_MESSAGE = "NOT FOUND";
    private static final String FAILED_MESSAGE = "An error occurred in server.";
    private static final String INVALID_ACCESS_TOKEN = "Invalid access token.";
    private static final String INVALID_REFRESH_TOKEN = "Invalid refresh token.";
    private static final String NOT_EXPIRED_TOKEN_YET = "Not expired token yet.";

    private final ApiResponseHeader header;
    private final Map<String, T> body;

    public static <T> ApiResponse<T> success(String name, T body) {
        return new ApiResponse(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), Map.of(name, body));
    }

    public static <T> ApiResponse<T> fail() {
        return new ApiResponse(new ApiResponseHeader(FAILED, FAILED_MESSAGE), null);
    }

    public static <T> ApiResponse<T> invalidAccessToken() {
        return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_ACCESS_TOKEN), null);
    }

    public static <T> ApiResponse<T> invalidRefreshToken() {
        return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_REFRESH_TOKEN), null);
    }

    public static <T> ApiResponse<T> notExpiredTokenYet() {
        return new ApiResponse(new ApiResponseHeader(FAILED, NOT_EXPIRED_TOKEN_YET), null);
    }

}
