package com.sep.sep_backend;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.auth.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class SepBackendApplicationTests {


    /** Mock of JwtUtil so the real JWT configuration is not required. */
    @MockitoBean
    private JwtUtil jwtUtil;

    /** Mock of RefreshTokenService which depends on JWT config and DB. */
    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JavaMailSender javaMailSender;


    @Test
	void contextLoads() {
	}

}
