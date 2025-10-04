package br.com.postech.auth.domain.ports.in;

import br.com.postech.auth.adapter.in.rest.dto.RequestLoginDto;
import br.com.postech.auth.adapter.in.rest.dto.TokenResponse;

public interface ValidUserPortIn {
    public TokenResponse generateToken(RequestLoginDto requestLoginDto);
}
