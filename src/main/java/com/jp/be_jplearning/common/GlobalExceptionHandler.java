package com.jp.be_jplearning.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
                String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message(errorMessage).data(null).build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message(ex.getMessage()).data(null).build(),
                                HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BusinessException.class)
        public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message(ex.getMessage()).data(null).build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(UsernameNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message("Tài khoản không tồn tại").data(null).build(),
                                HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message("Mật khẩu không đúng").data(null).build(),
                                HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
        public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
                        org.springframework.security.core.AuthenticationException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message("Xác thực thất bại").data(null).build(),
                                HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
                        org.springframework.security.access.AccessDeniedException ex) {
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message("Bạn không có quyền truy cập tài nguyên này").data(null).build(),
                                HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
                log.warn("Invalid argument: {}", ex.getMessage());
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false).message("Giá trị không hợp lệ: " + ex.getMessage()).data(null).build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
                log.error("Data integrity violation", ex);
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false)
                                .message("Dữ liệu vi phạm ràng buộc. Vui lòng kiểm tra lại.")
                                .data(null).build(),
                                HttpStatus.CONFLICT);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
                log.warn("Malformed request body: {}", ex.getMessage());
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false)
                                .message("Request body không hợp lệ. Vui lòng kiểm tra định dạng JSON.")
                                .data(null).build(),
                                HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
                log.error("Unhandled exception", ex);
                return new ResponseEntity<>(ApiResponse.<Void>builder()
                                .success(false)
                                .message("Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.")
                                .data(null).build(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
