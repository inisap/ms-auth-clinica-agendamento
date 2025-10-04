package br.com.postech.auth.adapter.in.rest;

import br.com.postech.auth.adapter.in.rest.dto.CreateUserResponse;
import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;
import br.com.postech.auth.adapter.in.rest.dto.RequestLoginDto;
import br.com.postech.auth.adapter.in.rest.dto.TokenResponse;
import br.com.postech.auth.application.usecases.CreateUserUseCase;
import br.com.postech.auth.application.usecases.ValidUserUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private ValidUserUseCase validUserService;

    @Mock
    private CreateUserUseCase createUserService;

    @Test
    void deveValidarLoginERetornarToken() throws Exception {
        // Arrange
        RequestLoginDto request = new RequestLoginDto("user1", "123");
        TokenResponse tokenResponse = TokenResponse.builder().accessToken("fake-token").build();

        when(validUserService.generateToken(any(RequestLoginDto.class)))
                .thenReturn(tokenResponse);

        // Act & Assert
        var retorno = controller.validaLogin(request);

        assertEquals(retorno.getStatusCode().value(), 200);
    }

    @Test
    void deveCriarUsuarioERetornarResponse() throws Exception {
        // Arrange
        RequestCreateUserDto request = RequestCreateUserDto.builder()
                .userName("maria da silva")
                .password("1234")
                .login("masilva")
                .build();

        CreateUserResponse response = CreateUserResponse.builder()
                .idUsuario(1L)
                .nome("maria da silva")
                .login("masilva")
                .build();

        when(createUserService.create(any(RequestCreateUserDto.class)))
                .thenReturn(response);

        // Act & Assert
        var retorno = controller.createNewUser(request);

        assertEquals(retorno.getStatusCode().value(), 200);
    }
}
