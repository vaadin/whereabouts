package com.example.application.security;

import com.example.application.base.domain.UserPrincipalRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@NullMarked
class AppUserDetailsService implements UserDetailsService {

    private final UserPrincipalRepository userPrincipalRepository;

    AppUserDetailsService(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userPrincipalRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
