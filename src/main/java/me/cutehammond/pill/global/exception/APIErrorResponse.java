package me.cutehammond.pill.global.exception;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

/**
 * 서버 단에서 클라이언트 단으로 보내는 Error Response의 명세입니다.
 */
@Slf4j
public final class APIErrorResponse {

    @Getter
    private final ErrorCode errorCode;

    @Getter
    @JsonIgnore
    private final HttpStatus httpStatus;

    @Getter
    private final String specificCode;

    @Getter
    private final String message;

    private final Map<String, String> extra;

    @Builder
    public APIErrorResponse(@NonNull ErrorCode errorCode, @NonNull HttpStatus httpStatus, @NonNull String specificCode, @NonNull String message, Map<String, String> extra) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.specificCode = specificCode;
        this.message = message;
        this.extra = extra == null ? Map.of() : Collections.unmodifiableMap(extra);
    }

    @JsonAnyGetter
    public Map<String, String> getExtra() {
        return extra;
    }

    /**
     * 컨트롤러 단을 통해 보내지 않고 직접 HttpServletResponse에 실어 보낼 때 사용됩니다.
     * @throws IOException
     */
    public static void sendJSONResponse(HttpServletResponse httpServletResponse, @NonNull APIErrorResponse errorResponse) throws IOException {
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(errorResponse.getHttpStatus().value());

        try(OutputStream os = httpServletResponse.getOutputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();

            os.write(objectMapper.writeValueAsString(errorResponse).getBytes());
            os.flush();
        }
    }

}
