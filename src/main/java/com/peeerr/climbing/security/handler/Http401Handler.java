package com.peeerr.climbing.security.handler;

import static jakarta.servlet.http.HttpServletResponse.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.dto.common.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@RequiredArgsConstructor
public class Http401Handler implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        mapper.writeValue(response.getWriter(), ApiResponse.of(ErrorMessage.LOGIN_REQUIRED));
    }

}
