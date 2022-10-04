package me.cutehammond.pill.global.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiResponseType {

    /* Authentication */
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Invalid access token."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Invalid refresh token."),

    NO_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh token is not found."),
    NO_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "Access token is not found."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Need to authorize.");

    @NonNull
    private final HttpStatus status;

    @NonNull
    private final String message;

}
