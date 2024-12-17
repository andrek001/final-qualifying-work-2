package com.netology.diplom.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.netology.diplom.auth.AuthUser;
import com.netology.diplom.entity.User;
import com.netology.diplom.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByLogin(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        } else {
            User user = userOptional.get();
            return new AuthUser(user.getId(), user.getLogin(), user.getLogin(), user.getLogin(), user.getPassword(), null, true);
        }
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
