package me.cutehammond.pill.global.exception.handler;

import me.cutehammond.pill.global.exception.APIErrorResponse;
import me.cutehammond.pill.global.exception.ErrorCode;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class HttpErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<APIErrorResponse> handleError(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.getStatus());
        APIErrorResponse apiErrorResponse = APIErrorResponse.builder()
                .errorCode(httpStatus.is4xxClientError() ? ErrorCode.SPRING_BAD_REQUEST : ErrorCode.SPRING_INTERNAL_ERROR)
                .httpStatus(httpStatus)
                .specificCode(Integer.toString(response.getStatus()))
                .message("")
                .extra(Map.of("request_uri", (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)))
                .build();

        return ResponseEntity.status(httpStatus).body(apiErrorResponse);
    }

}
