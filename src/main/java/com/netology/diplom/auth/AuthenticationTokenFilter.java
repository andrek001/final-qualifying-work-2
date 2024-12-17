package com.netology.diplom.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.netology.diplom.service.AuthService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter implements Filter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthProvider jwtProvider;

    @SneakyThrows
    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain chain) {
        final String authToken = request.getHeader("auth-token");


        final String username = jwtProvider.getUsernameFromToken(authToken);
        log.info("checking authentication for user " + username);
        if ((authToken != null) && authService.isTokenExist(authToken) && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (Boolean.TRUE.equals(jwtProvider.validateToken(authToken, userDetails))) {
                    // Double check for valid token both not active user
                    if (userDetails.isEnabled()) {
                        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        log.info("authenticated user " + username + ", setting security context");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.getContext()
                                .setAuthentication(UsernamePasswordAuthenticationToken.unauthenticated(userDetails, null));


                    }
                }
            } catch (final UsernameNotFoundException usernameNotFoundException) {
                // still ignore UnauthorizedException, token will be empty, status unauthorized
            }

        } else {
            log.info("{}", SecurityContextHolder.getContext().getAuthentication());
        }
        chain.doFilter(request, response);
    }

}