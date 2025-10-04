package br.com.postech.auth.application.usecases;

import br.com.postech.auth.adapter.in.rest.dto.RequestLoginDto;
import br.com.postech.auth.adapter.in.rest.dto.TokenResponse;
import br.com.postech.auth.adapter.out.persistence.RoleEntity;
import br.com.postech.auth.domain.exception.LoginSenhaInvalidoException;
import br.com.postech.auth.domain.ports.in.ValidUserPortIn;
import br.com.postech.auth.domain.ports.out.UserRepositoryPortOut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ValidUserUseCase implements ValidUserPortIn {

    private final UserRepositoryPortOut userRepository;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long ttlSeconds;

    public ValidUserUseCase(UserRepositoryPortOut userRepositoryPortOut,
                            JwtEncoder jwtEncoder,
                            @Value("${app.security.issuer}") String issuer,
                            @Value("${app.security.access-token.ttl-seconds}") long ttlSeconds){
        this.userRepository = userRepositoryPortOut;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.ttlSeconds = ttlSeconds;
    }

    @Override
    public TokenResponse generateToken(RequestLoginDto requestLoginDto){
        var user = userRepository.findByLogin(requestLoginDto.getLogin());

        if(user.isEmpty() || !user.get().getPassword().equals(requestLoginDto.getPassword())){
            throw new LoginSenhaInvalidoException();
        }

        var now = Instant.now();

        var roles = user.get().getRoles() == null ?
                java.util.List.of() :
                user.get().getRoles().stream()
                        .map(RoleEntity::getName)
                        .toList();

        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.get().getLogin())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ttlSeconds))
                .claim("roles", roles)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return TokenResponse.builder()
                .accessToken(jwtValue)
                .build();
    }

}
