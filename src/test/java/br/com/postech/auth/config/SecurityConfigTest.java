package br.com.postech.auth.config;

import br.com.postech.auth.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityConfigTest {

    private SecurityConfig securityConfig;
    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;


    @BeforeEach
    void setUp() throws Exception {
        // gera par de chaves RSA para o teste
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        publicKey = (RSAPublicKey) pair.getPublic();
        privateKey = (RSAPrivateKey) pair.getPrivate();

        // converte para PEM
        String publicPem = "-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----";

        String privatePem = "-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded()) +
                "\n-----END PRIVATE KEY-----";

        // injeta no SecurityConfig via ByteArrayResource
        securityConfig = new SecurityConfig();
        securityConfig.setPublicPem(new ByteArrayResource(publicPem.getBytes()));
        securityConfig.setPrivatePem(new ByteArrayResource(privatePem.getBytes()));
    }

    @Test
    void deveCriarPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String senha = "123456";
        String hash = encoder.encode(senha);

        assertThat(encoder.matches(senha, hash)).isTrue();
    }

    @Test
    void deveGerarETestarJwtEncoderDecoder() throws Exception {
        JwtEncoder encoder = securityConfig.jwtEncoder();
        JwtDecoder decoder = securityConfig.jwtDecoder();

        var claims = JwtClaimsSet.builder()
                .issuer("issuer-test")
                .subject("usuario123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("roles", "ENFERMEIRO")
                .build();

        // gera token
        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        assertThat(token).isNotBlank();

        // valida token
        Jwt decoded = decoder.decode(token);
        assertThat(decoded.getSubject()).isEqualTo("usuario123");

    }
}