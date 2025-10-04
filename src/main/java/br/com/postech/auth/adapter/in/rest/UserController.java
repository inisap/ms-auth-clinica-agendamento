package br.com.postech.auth.adapter.in.rest;

import br.com.postech.auth.adapter.in.rest.dto.CreateUserResponse;
import br.com.postech.auth.adapter.in.rest.dto.RequestCreateUserDto;
import br.com.postech.auth.adapter.in.rest.dto.RequestLoginDto;
import br.com.postech.auth.adapter.in.rest.dto.TokenResponse;
import br.com.postech.auth.application.usecases.CreateUserUseCase;
import br.com.postech.auth.application.usecases.ValidUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final ValidUserUseCase validUserService;
    private final CreateUserUseCase createUserService;

    public UserController(ValidUserUseCase validUserService,
                          CreateUserUseCase createUserService){
        this.validUserService = validUserService;
        this.createUserService = createUserService;
    }

    @PostMapping("/auth/logins")
    public ResponseEntity<TokenResponse> validaLogin(
            @RequestBody RequestLoginDto requestLoginDto
    ) {

        var tokenValid = validUserService.generateToken(requestLoginDto);

        return ResponseEntity.ok().body(tokenValid);
    }

    @PostMapping()
    public ResponseEntity<CreateUserResponse> createNewUser(
            @RequestBody RequestCreateUserDto requestCreateUserDto
    ) {
        var userResponse = createUserService.create(requestCreateUserDto);

        return ResponseEntity.ok().body(userResponse);
    }

}
