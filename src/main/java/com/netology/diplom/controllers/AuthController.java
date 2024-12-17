package com.netology.diplom.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netology.diplom.auth.AuthProvider;
import com.netology.diplom.auth.AuthRequest;
import com.netology.diplom.auth.Login;
import com.netology.diplom.entity.User;
import com.netology.diplom.service.AuthService;
import com.netology.diplom.service.UserService;

import lombok.NonNull;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final AuthProvider jwtProvider;
	private final AuthService authService;

	public AuthController(PasswordEncoder passwordEncoder, UserService userService, AuthProvider jwtProvider,
			AuthService authService) {
		this.passwordEncoder = passwordEncoder;
		this.userService = userService;
		this.jwtProvider = jwtProvider;
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<Login> login(@RequestBody AuthRequest jwtRequest) {
		Optional<Login> loginOptional = authService.login(jwtRequest.getLogin(), jwtRequest.getPassword());
		return loginOptional.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@PostMapping("/register")
	public ResponseEntity<Void> register(@RequestBody AuthRequest jwtRequest) {
		User user =new User();
		user.setLogin(jwtRequest.getLogin());
		user.setPassword(jwtRequest.getPassword());
		User user1 = authService.register(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestHeader("auth-token") @NonNull String token) {
		authService.logout(token);
		return ResponseEntity.ok().build();
	}
}
