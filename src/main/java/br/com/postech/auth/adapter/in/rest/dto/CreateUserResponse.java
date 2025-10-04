package br.com.postech.auth.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateUserResponse {

    private Long idUsuario;
    private String nome;
    private String login;
    private Boolean usuarioAtivo;

}
