package com.peeerr.climbing.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.dto.request.MemberLoginRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class MemberAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper mapper;

    public MemberAuthenticationFilter(String defaultFilterProcessesUrl, ObjectMapper mapper) {
        super(defaultFilterProcessesUrl);
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        MemberLoginRequest loginRequest = mapper.readValue(request.getInputStream(), MemberLoginRequest.class);

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
        token.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(token);
    }

}
