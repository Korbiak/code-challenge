package com.korbiak.challenge.service;

import com.korbiak.challenge.model.User;

public interface UserService {

    User findUserByName(String name);
}
