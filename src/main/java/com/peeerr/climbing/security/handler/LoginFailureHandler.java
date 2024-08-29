package com.peeerr.climbing.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.dto.common.ErrorResponse;
import com.peeerr.climbing.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(SC_BAD_REQUEST);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(SC_BAD_REQUEST)
                .message(ErrorCode.LOGIN_FAILED.getMessage())
                .build();

        mapper.writeValue(response.getWriter(), errorResponse);
    }

}
