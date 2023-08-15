package com.akagiyui.drive.config;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.ResponseResult;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.common.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.akagiyui.common.ResponseEnum.*;


/**
 * 全局异常处理器
 *
 * @author AkagiYui
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    /**
     * 404 API未找到
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
    })
    public ResponseResult<?> noRouteException(Exception ignored) {
        return ResponseResult.response(NOT_FOUND);
    }


    /**
     * 400 请求体错误
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MaxUploadSizeExceededException.class,
            MethodArgumentNotValidException.class,
    })
    public ResponseResult<?> badRequestException(Exception e) {
        // 目前可预见的是 JSON 解析错误
        Throwable cause = e.getCause();
        if (cause != null) {
            return ResponseResult.response(BAD_REQUEST, cause.getMessage());
        }
        // 无请求体错误
        if (e.getMessage() != null && e.getMessage().startsWith("Required request body is missing")) {
            return ResponseResult.response(BAD_REQUEST, "Request body is missing");
        }
        // 参数校验异常处理
        if (e instanceof MethodArgumentNotValidException ae) {
            FieldError fieldError = ae.getBindingResult().getFieldError();
            if (fieldError != null) {
                String message = fieldError.getDefaultMessage();
                // 国际化时匹配到未适配的字段，去除前后的大括号
                if (message != null && message.startsWith("{") && message.endsWith("}")) {
                    message = message.substring(1, message.length() - 1);
                }
                return ResponseResult.response(BAD_REQUEST, message);
            }
            return ResponseResult.response(BAD_REQUEST);
        }
        return ResponseResult.response(BAD_REQUEST, e.getMessage());
    }

    /**
     * 429 请求过快
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseResult<?> tooManyRequestsException(TooManyRequestsException ignored) {
        return ResponseResult.response(TOO_MANY_REQUESTS);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<?> unknownException(Exception e) {
        // 自定义异常处理
        if (e instanceof CustomException ce) {
            return ResponseResult.response(ce.getStatus());
        }

        log.error("Unknown exception", e);
        return ResponseResult.response(INTERNAL_ERROR);
    }

    /**
     * 处理 Spring Security 请求拒绝异常
     *
     * @return RequestRejectedHandler
     */
    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return new HttpStatusRequestRejectedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, RequestRejectedException requestRejectedException) {
                ResponseResult.writeResponse(response, HttpStatus.BAD_REQUEST, ResponseEnum.BAD_REQUEST);
            }
        };
    }

    /**
     * 处理 Spring Security 访问拒绝异常
     *
     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> ResponseResult.writeResponse(response, HttpStatus.FORBIDDEN, ResponseEnum.FORBIDDEN);
    }

    /**
     * 处理 Spring Security 认证异常
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> ResponseResult.writeResponse(response, HttpStatus.UNAUTHORIZED, ResponseEnum.UNAUTHORIZED);
    }
}
