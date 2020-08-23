package com.korbiak.challenge.service.impl;

import com.korbiak.challenge.model.User;
import com.korbiak.challenge.repository.UserRepository;
import com.korbiak.challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserByName(String name) {
        log.info("Getting user by name = " + name);
        return userRepository.findUserByName(name)
                .orElseThrow(() -> {
                    log.error("User with name = " + name + " not found");
                    return new EntityNotFoundException("User with name = " + name + " not found");
                });
    }
}
