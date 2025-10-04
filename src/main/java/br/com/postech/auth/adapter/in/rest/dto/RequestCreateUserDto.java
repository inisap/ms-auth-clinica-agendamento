package br.com.postech.auth.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestCreateUserDto {

    private String userName;
    private String password;
    private String login;

}
