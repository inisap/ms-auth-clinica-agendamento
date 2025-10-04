package br.com.postech.auth.application.usecases;

import br.com.postech.auth.adapter.in.rest.dto.CreateUserResponse;
import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;
import br.com.postech.auth.adapter.in.rest.mapper.UserMapper;
import br.com.postech.auth.domain.ports.in.CreateUserPortIn;
import br.com.postech.auth.domain.ports.out.UserRepositoryPortOut;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase implements CreateUserPortIn {

    private final UserRepositoryPortOut userRepositoryPortOut;

    public CreateUserUseCase(UserRepositoryPortOut userRepositoryPortOut){
        this.userRepositoryPortOut = userRepositoryPortOut;

    }

    @Override
    public CreateUserResponse create(RequestCreateUserDto requestCreateUserDto){

        var entity = UserMapper.createUserDtoToEntity(requestCreateUserDto);

        var savedEntity = userRepositoryPortOut.save(entity);

        return UserMapper.entityUserToCreateUserResponse(savedEntity);
    }
}
