package com.netology.diplom.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.netology.diplom.exceptions.UserExistsException;
import com.netology.diplom.exceptions.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.netology.diplom.auth.AuthProvider;
import com.netology.diplom.auth.Login;
import com.netology.diplom.entity.User;
import com.netology.diplom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final AuthProvider jwtProvider;

	private final Map<String, UserDetails> tokenStorage = new ConcurrentHashMap<>();

	public void logout(String authToken) {
		tokenStorage.remove(authToken);
	}

	public User register(User user) {
		userRepository.findByLogin(user.getLogin())
				.ifPresent(u -> {
					throw new UserExistsException("Пользователь уже существует");
				});
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public boolean isTokenExist(String token) {
		return tokenStorage.get(token) != null;
	}

	public Optional<Login> login(String loginName, String password) {
		UserDetails userDetails = userService.loadUserByUsername(loginName);
		if (userDetails != null && passwordEncoder.matches(password, userDetails.getPassword())) {
			String token = jwtProvider.generateAccessToken(userDetails);
			Login login = new Login();
			login.setAuthToken(token);
			tokenStorage.put(token, userDetails);
			return Optional.of(login);
		}
		return Optional.empty();
	}

}

