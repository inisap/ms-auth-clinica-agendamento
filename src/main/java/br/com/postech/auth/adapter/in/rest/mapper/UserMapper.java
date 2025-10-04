package br.com.postech.auth.adapter.in.rest.mapper;

import br.com.postech.auth.adapter.in.rest.dto.CreateUserResponse;
import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;
import br.com.postech.auth.adapter.out.persistence.UserEntity;

public class UserMapper {

    public static UserEntity createUserDtoToEntity(RequestCreateUserDto createUserDto){

        return UserEntity.builder()
                .username(createUserDto.getUserName())
                .password(createUserDto.getPassword())
                .login(createUserDto.getLogin())
                .enabled(true)
                .build();
    }

    public static CreateUserResponse entityUserToCreateUserResponse(UserEntity entity){
        return CreateUserResponse.builder()
                .idUsuario(entity.getId())
                .nome(entity.getUsername())
                .login(entity.getLogin())
                .usuarioAtivo(entity.isEnabled())
                .build();
    }
}
