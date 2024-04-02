package com.peeerr.climbing.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();

        String errorResponse = mapper.writeValueAsString(ApiResponse.of(ErrorMessage.LOGIN_REQUIRED));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorResponse);

        response.getWriter().flush();
        response.getWriter().close();
    }

}