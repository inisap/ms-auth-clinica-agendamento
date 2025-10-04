package br.com.postech.auth.domain.exception;

public class LoginSenhaInvalidoException extends RuntimeException {

    public LoginSenhaInvalidoException() {
        super ("Usuario ou Senha Invalido");
    }
}
