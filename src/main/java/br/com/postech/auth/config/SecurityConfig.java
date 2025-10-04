package br.com.postech.auth.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
@Setter
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Value("${app.security.keys.private-pem}")
    private org.springframework.core.io.Resource privatePem;

    @Value("${app.security.keys.public-pem}")
    private org.springframework.core.io.Resource publicPem;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,
                                "/v1/users/auth/logins",
                                "/v1/users")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        // Recupera as chaves do Secrets Manager
        String privateKeyPem = readPemAsString(privatePem);
        String publicKeyPem  = readPemAsString(publicPem);

        // Converte as chaves de Base64 para objetos RSAPrivateKey e RSAPublicKey
        RSAPrivateKey privateKey = convertToPrivateKey(privateKeyPem);
        RSAPublicKey publicKey = convertToPublicKey(publicKeyPem);

        JWK jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // Recupera a chave pública do Secrets Manager
        String publicKeyPem = readPemAsString(publicPem);
        RSAPublicKey publicKey = convertToPublicKey(publicKeyPem);
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String readPemAsString(org.springframework.core.io.Resource res) throws Exception {
        try (InputStream is = res.getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.US_ASCII);
        }
    }

    private RSAPrivateKey convertToPrivateKey(String key) throws Exception {
        var keyclean = key.replaceAll("(\r\n|\n|\r)", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s","").trim();
        byte[] keyBytes = Base64.getDecoder().decode(keyclean);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private RSAPublicKey convertToPublicKey(String key) throws Exception {
        var keyclean = key.replaceAll("(\r\n|\n|\r)", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s","").trim();
        byte[] keyBytes = Base64.getDecoder().decode(keyclean);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

}
