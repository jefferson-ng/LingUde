package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.user.dto.SigninRequest;
import com.sep.sep_backend.user.dto.SignupRequest;
import com.sep.sep_backend.user.dto.AuthResponse;
import com.sep.sep_backend.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest req) {
        AuthResponse created = userService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest req) {
        AuthResponse res = userService.signin(req);
        return ResponseEntity.ok(res);
    }
}
