package br.com.postech.auth.application.usecases;

import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;
import br.com.postech.auth.adapter.out.persistence.UserEntity;
import br.com.postech.auth.domain.ports.out.UserRepositoryPortOut;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateUserUseCaseTest {

    @Mock
    private UserRepositoryPortOut userRepositoryPortOut;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    void criaUsuarioComSucesso(){
        //arrange
        String login = "masilva";
        String password = "1234";
        String userName = "1234";

        RequestCreateUserDto requestCreateUserDto =  RequestCreateUserDto.builder()
                .userName(userName)
                .login(login)
                .password(password)
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .enabled(true)
                .login(login)
                .username(userName)
                .build();

        when(userRepositoryPortOut.save(any())).thenReturn(userEntity);

        //act
        var retorno = createUserUseCase.create(requestCreateUserDto);

        //assert
        assertEquals(retorno.getIdUsuario(), 1L);
        assertEquals(retorno.getLogin(), login);
        assertEquals(retorno.getNome(), userName);
        assertEquals(retorno.getUsuarioAtivo(), true);


    }
}
