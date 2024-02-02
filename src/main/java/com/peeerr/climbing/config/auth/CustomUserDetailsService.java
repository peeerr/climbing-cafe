package com.peeerr.climbing.config.auth;

import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.domain.user.User;
import com.peeerr.climbing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }

}
