package br.com.dbc.wbhealth.controller;

import br.com.dbc.wbhealth.documentation.AuthControllerDoc;
import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.usuario.*;
import br.com.dbc.wbhealth.exceptions.RegraDeNegocioException;
import br.com.dbc.wbhealth.security.AuthenticationService;
import br.com.dbc.wbhealth.security.TokenService;
import br.com.dbc.wbhealth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthControllerDoc {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<UsuarioOutputDTO> getLoggedUser() throws RegraDeNegocioException, EntityNotFound {
        return new ResponseEntity<>(usuarioService.getLoggedUser(), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UsuarioLoginInputDTO usuario) throws RegraDeNegocioException{
        String tokenAutenticado = usuarioService.login(usuario, authenticationManager, tokenService);
        return new ResponseEntity<>(tokenAutenticado, HttpStatus.OK);
    }

    @PostMapping("/create-user")
    public ResponseEntity<UsuarioOutputDTO> create(@RequestBody @Valid UsuarioInputDTO usuario)
            throws RegraDeNegocioException, EntityNotFound {
        return new ResponseEntity<>(usuarioService.create(usuario), HttpStatus.OK);
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioOutputDTO> update(@PathVariable("idUsuario") Integer idUsuario,
                                                   @Valid @RequestBody UsuarioInputDTO usuario)
            throws EntityNotFound {
        return new ResponseEntity<>(usuarioService.update(idUsuario, usuario), HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UsuarioSenhaInputDTO usuario)
            throws EntityNotFound, RegraDeNegocioException {
        usuarioService.updatePassword(usuario);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> remove(@PathVariable("idUsuario") Integer idUsuario) throws EntityNotFound {
        usuarioService.remove(idUsuario);
        return ResponseEntity.ok().build();
    }

}
