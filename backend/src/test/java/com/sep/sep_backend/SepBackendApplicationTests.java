package com.sep.sep_backend;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.auth.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class SepBackendApplicationTests {


    /** Mock of JwtUtil so the real JWT configuration is not required. */
    @MockBean
    private JwtUtil jwtUtil;

    /** Mock of RefreshTokenService which depends on JWT config and DB. */
    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JavaMailSender javaMailSender;


    @Test
	void contextLoads() {
	}

}
