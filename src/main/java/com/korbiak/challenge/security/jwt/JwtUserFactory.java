package com.korbiak.challenge.security.jwt;

import com.korbiak.challenge.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getName(),
                user.getPassword(),
                mapToGrantedAuthorities(user.isAdminPermissions())
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(boolean adminPermissions) {
        SimpleGrantedAuthority authority;
        if (adminPermissions) {
            authority = new SimpleGrantedAuthority("ADMIN");
        } else {
            authority = new SimpleGrantedAuthority("USER");
        }
        return Collections.singletonList(authority);
    }
}
