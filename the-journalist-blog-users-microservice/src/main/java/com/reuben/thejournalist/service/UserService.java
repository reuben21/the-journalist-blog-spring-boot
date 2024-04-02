package com.reuben.thejournalist.service;

import com.reuben.thejournalist.model.UserEntity;
import com.reuben.thejournalist.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userDetails = userEntityRepository.findUserByEmail(username.toLowerCase());
        if (userDetails.isEmpty()) {
            throw new UsernameNotFoundException("User with email " + username + " not found");
        } else {
            return  User.builder().username(userDetails.get().getEmail())
                    .password(userDetails.get().getPassword())
                    .roles(userDetails.get().getRole().stream().map(Enum::name).toArray(String[]::new)) // Convert the roles to an array of strings and pass it to the roles method of the User builder
                    .build();
        }
    }
}
