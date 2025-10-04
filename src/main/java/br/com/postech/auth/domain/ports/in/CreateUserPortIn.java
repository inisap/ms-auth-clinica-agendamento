package br.com.postech.auth.domain.ports.in;

import br.com.postech.auth.adapter.in.rest.dto.CreateUserResponse;
import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;

public interface CreateUserPortIn {
    public CreateUserResponse create(RequestCreateUserDto requestCreateUserDto);
}
