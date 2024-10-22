package com.webest.web.exception;

import static com.webest.web.exception.ErrorCode.INVALID_INPUT;
import static com.webest.web.exception.ErrorCode.SERVER_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.webest.web.exception.custom.InvalidMethodResponseDto;
import com.webest.web.response.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<? extends CommonResponse<?>> applicationException(
        ApplicationException exception
    ) {

        return ResponseEntity.status(exception.getHttpStatus())
            .body(CommonResponse.error(exception.getHttpStatus(), exception.getMessage()));
    }

    /**
     * Method의 Argument의 Validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<? extends CommonResponse<?>> handlerValidationException(
        MethodArgumentNotValidException exception) {

        if (exception.getCause() instanceof ApplicationException) {
            return ResponseEntity.status(BAD_REQUEST)
                .body(CommonResponse.error(INVALID_INPUT.getStatus(), INVALID_INPUT.getMessage()));
        }

        List<InvalidMethodResponseDto> invalidInputResList = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(ex -> new InvalidMethodResponseDto(ex.getField(),
                ex.getDefaultMessage())) // defaultMessage 필드명을 message 변경
            .toList();

        return ResponseEntity.status(BAD_REQUEST)
            .body(CommonResponse.error(INVALID_INPUT.getStatus(), INVALID_INPUT.getMessage(),
                invalidInputResList));
    }

    /**
     * Input 파라미터(현재 Body라 생각)가 잘못되었을 때 전달
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<? extends CommonResponse<?>> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException exception) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.error(INVALID_INPUT.getStatus(),
                "잘못 된 입력 데이터 입니다.", exception.getMessage()));
    }

    /**
     * Request Param 및 Header
     */
    @ExceptionHandler({
        MissingRequestHeaderException.class,
        MissingServletRequestParameterException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<? extends CommonResponse<?>> handleRequestHeaderMissing(
        Exception exception) {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.error(INVALID_INPUT.getStatus(),
                "잘못된 입력 데이터 입니다.", exception.getMessage()));
    }

    @ExceptionHandler({
        NoResourceFoundException.class, 
        HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<? extends CommonResponse<?>> handleNoResourceFoundException(
        Exception exception
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(CommonResponse.error(INVALID_INPUT.getStatus(),
                "잘못된 URL 입력입니다.", exception.getMessage()));
    }


    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<? extends CommonResponse<?>> handleException(Exception e) {

        // 예상 치 못한 에러
        log.error("예상치 못한 에러 : ", e);

        return ResponseEntity.status(SERVER_ERROR.getStatus())
            .body(CommonResponse.error(SERVER_ERROR.getStatus(), SERVER_ERROR.getMessage()));
    }

}
