package br.com.postech.auth.application.usecases;

import br.com.postech.auth.adapter.in.rest.dto.RequestLoginDto;
import br.com.postech.auth.adapter.in.rest.dto.TokenResponse;
import br.com.postech.auth.adapter.out.persistence.RoleEntity;
import br.com.postech.auth.domain.exception.LoginSenhaInvalidoException;
import br.com.postech.auth.domain.ports.out.UserRepositoryPortOut;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidUserUseCaseTest {

    @Mock
    private UserRepositoryPortOut userRepository;

    @Mock
    private JwtEncoder jwtEncoder;

    private ValidUserUseCase validUserUseCase;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        validUserUseCase = new ValidUserUseCase(userRepository, jwtEncoder,
                "http://issuer-test", 3600);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Arrange
        RequestLoginDto loginDto = new RequestLoginDto("user1", "123");
        when(userRepository.findByLogin("user1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> validUserUseCase.generateToken(loginDto))
                .isInstanceOf(LoginSenhaInvalidoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        // Arrange
        var userEntity = new br.com.postech.auth.adapter.out.persistence.UserEntity();
        userEntity.setLogin("user1");
        userEntity.setPassword("senha_correta");

        RequestLoginDto loginDto = new RequestLoginDto("user1", "senha_errada");

        when(userRepository.findByLogin("user1")).thenReturn(Optional.of(userEntity));

        // Act & Assert
        assertThatThrownBy(() -> validUserUseCase.generateToken(loginDto))
                .isInstanceOf(LoginSenhaInvalidoException.class);
    }

    @Test
    void deveGerarTokenQuandoUsuarioValido() {
        // Arrange
        var userEntity = new br.com.postech.auth.adapter.out.persistence.UserEntity();
        userEntity.setLogin("user1");
        userEntity.setPassword("123");

        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("ENFERMEIRO");

        // aqui é o ajuste -> Set<RoleEntity> ao invés de cast incorreto
        userEntity.setRoles(Set.of(role));

        RequestLoginDto loginDto = new RequestLoginDto("user1", "123");

        when(userRepository.findByLogin("user1")).thenReturn(Optional.of(userEntity));

        // mock do JwtEncoder
        Jwt jwt = Jwt.withTokenValue("fake-jwt-token")
                .header("alg", "none")
                .claim("roles", List.of("ENFERMEIRO"))
                .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        TokenResponse response = validUserUseCase.generateToken(loginDto);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("fake-jwt-token");
    }

}
