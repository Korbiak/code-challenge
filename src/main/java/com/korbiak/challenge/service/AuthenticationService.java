package com.korbiak.challenge.service;


import com.korbiak.challenge.dto.request.AuthenticationRequest;
import com.korbiak.challenge.dto.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
