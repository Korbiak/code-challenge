package com.korbiak.challenge.service.impl;

import com.korbiak.challenge.dto.request.AuthenticationRequest;
import com.korbiak.challenge.dto.response.AuthenticationResponse;
import com.korbiak.challenge.model.User;
import com.korbiak.challenge.security.jwt.JwtTokenProvider;
import com.korbiak.challenge.service.AuthenticationService;
import com.korbiak.challenge.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
@Data
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private BCryptPasswordEncoder encoder;

    @PostConstruct
    private void initEncoder() {
        encoder = new BCryptPasswordEncoder();
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        log.info("Logging user with name = " + authenticationRequest.getUsername());
        User user = userService.findUserByName(authenticationRequest.getUsername());
        if (!encoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            log.error("Password not correct");
            throw new IllegalArgumentException("Password not correct");
        }

        String token = jwtTokenProvider.createToken(user.getName(), user.isAdminPermissions());
        return new AuthenticationResponse(
                user.getName(),
                token
        );
    }
}
