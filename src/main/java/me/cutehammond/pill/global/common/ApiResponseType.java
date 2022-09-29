package me.cutehammond.pill.global.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiResponseType {

    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "Invalid access token."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "Invalid refresh token."),
    NO_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "Refresh token is not found."),
    NO_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "Access token is not found."),
    NOT_EXPIRED_TOKEN_YET(HttpStatus.BAD_REQUEST, "Not expired token yet.");

    @NonNull
    private final HttpStatus status;

    @NonNull
    private final String message;

}
