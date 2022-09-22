package me.cutehammond.pill.global.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class ApiResponse {

    public static <T> ResponseEntity<T> success(T body) {
        return ResponseEntity.ok(body);
    }

    public static <T> ResponseEntity<T> fail(T body) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    public static ResponseEntity getResponse(@NonNull ApiResponseType type) {
        return ResponseEntity
                .status(type.getStatus())
                .body(Map.of(
                        "type", type.name(),
                        "message", type.getMessage()
                ));
    }

}
