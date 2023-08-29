package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.exceptions.RegraDeNegocioException;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioLoginInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioOutputDTO;
import br.com.dbc.wbhealth.model.entity.CargoEntity;
import br.com.dbc.wbhealth.model.entity.UsuarioEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.UsuarioRepository;
import br.com.dbc.wbhealth.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    LogService logService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    CargoService cargoService;
    @Mock
    ObjectMapper objectMapper;

    static UsuarioEntity usuario = new UsuarioEntity();
    static UsuarioInputDTO usuarioInput = new UsuarioInputDTO();
    static UsuarioOutputDTO usuarioOutput = new UsuarioOutputDTO();
    static CargoEntity cargo = new CargoEntity();
//    CargoEntity cargo2 = new CargoEntity();


    @Test
    public void testFindById() throws EntityNotFound {
        UsuarioEntity usuarioInput = createUsuarioEntity();

        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuarioInput));

        UsuarioEntity usuarioEntity = usuarioService.findById(1);

        Assertions.assertNotNull(usuarioEntity);
        Assertions.assertEquals(usuarioInput, usuarioEntity);
    }

    @Test
    public void testFindByLogin() {
        UsuarioEntity usuarioInput = createUsuarioEntity();

        when(usuarioRepository.findByLogin("maria")).thenReturn(Optional.of(usuarioInput));

        Optional<UsuarioEntity> usuarioEntityOptional = usuarioService.findByLogin("maria");

        Assertions.assertTrue(usuarioEntityOptional.isPresent());

        UsuarioEntity usuarioEntity = usuarioEntityOptional.orElse(null);

        Assertions.assertNotNull(usuarioEntity);
        Assertions.assertEquals(usuarioInput, usuarioEntity);
    }

    @Test
    public void testGetIdLoggedUser() throws RegraDeNegocioException {
        // Simular autenticação do usuário
        Authentication auth = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Integer idUsuario = usuarioService.getIdLoggedUser();

        Assertions.assertEquals(1, idUsuario);
    }

    @Test
    public void testGetLoggedUser() throws RegraDeNegocioException, EntityNotFound {
        Authentication auth = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        UsuarioEntity usuario = createUsuarioEntity();
        UsuarioOutputDTO usuarioOutputDTO = createUsuarioOutputDTO();
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(usuario));

        when(objectMapper.convertValue(usuario, UsuarioOutputDTO.class)).thenReturn(usuarioOutputDTO);

        UsuarioOutputDTO usuarioLogado = usuarioService.getLoggedUser();

        Assertions.assertNotNull(usuarioLogado);
        Assertions.assertEquals("1", usuarioLogado.getIdUsuario());
    }

    @Test
    public void testLogin() throws RegraDeNegocioException {
        UsuarioLoginInputDTO usuarioLoginInputDTO = new UsuarioLoginInputDTO();
        usuarioLoginInputDTO.setLogin("05281583093");
        usuarioLoginInputDTO.setSenha("1234");

        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        TokenService tokenService = mock(TokenService.class);

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        UsuarioEntity usuarioValidado = createUsuarioEntity();

        when(authenticationMock.getPrincipal()).thenReturn(usuarioValidado);

        String tokenGerado = "mocked-token";
        when(tokenService.generateToken(usuarioValidado)).thenReturn(tokenGerado);

        String tokenRetornado = usuarioService.login(usuarioLoginInputDTO, authenticationManager, tokenService);

        Assertions.assertEquals(tokenRetornado, tokenGerado);

        verify(logService).create(Descricao.LOGIN, usuarioValidado.getIdUsuario());
    }

    private static UsuarioEntity createUsuarioEntity() {
        cargo.setIdCargo(4);
        cargo.setNome("MEDICO");
        Set<CargoEntity> cargos1 = new HashSet<>();

        usuario.setIdUsuario(1);
        usuario.setLogin("05281583093");
        usuario.setSenha("1234");
        usuario.setCargos(cargos1);
        cargos1.add(cargo);

        return usuario;
    }

    private static UsuarioInputDTO createUsuarioInputDTO() {
        Set<Integer> cargos = new HashSet<>();
        cargos.add(4);

        usuarioInput.setCargos(cargos);
        usuarioInput.setLogin("05281583093");

        return usuarioInput;
    }

    private static UsuarioOutputDTO createUsuarioOutputDTO() {
        Set<Integer> cargos = new HashSet<>();
        cargos.add(4);

        usuarioOutput.setIdUsuario("1");
        usuarioOutput.setCargos(cargos);
        usuarioOutput.setLogin("05281583093");

        return usuarioOutput;
    }
}