package com.ssh.greenthumb.api.controller.login;

import com.ssh.greenthumb.api.common.exception.BadRequestException;
import com.ssh.greenthumb.api.dao.user.UserRepository;
import com.ssh.greenthumb.api.domain.login.AuthProvider;
import com.ssh.greenthumb.api.domain.login.Role;
import com.ssh.greenthumb.api.domain.user.User;
import com.ssh.greenthumb.api.dto.login.ApiResponse;
import com.ssh.greenthumb.api.dto.login.AuthResponse;
import com.ssh.greenthumb.api.dto.login.LoginRequest;
import com.ssh.greenthumb.api.dto.login.SignUpRequest;
import com.ssh.greenthumb.api.security.CurrentUser;
import com.ssh.greenthumb.api.security.TokenProvider;
import com.ssh.greenthumb.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userDao;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public Object authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
//        response.setHeader("Authorization", token);

        User user = userDao.findByEmailAndIsDeleted(loginRequest.getEmail(), "n");

        return new ResponseEntity(AuthResponse.builder()
                .accessToken(token)
                .id(user.getId())
                .build(), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if(userDao.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("이미 해당 이메일을 사용하고 있습니다.");
        }

        User result = userDao.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickName(signUpRequest.getNickName())
                .provider(AuthProvider.local)
                .role(Role.USER)
                .build()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getEmail()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "계정 생성 성공"));
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userDao.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

}