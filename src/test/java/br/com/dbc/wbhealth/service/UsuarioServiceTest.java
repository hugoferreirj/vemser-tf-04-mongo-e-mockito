package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.exceptions.RegraDeNegocioException;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioLoginInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioOutputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioSenhaInputDTO;
import br.com.dbc.wbhealth.model.entity.CargoEntity;
import br.com.dbc.wbhealth.model.entity.UsuarioEntity;
import br.com.dbc.wbhealth.model.enumarator.Descricao;
import br.com.dbc.wbhealth.repository.UsuarioRepository;
import br.com.dbc.wbhealth.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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

    @BeforeEach
    private void setUp() {
        Authentication auth = new UsernamePasswordAuthenticationToken("1", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

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

        Assertions.assertNotNull(tokenRetornado);
        Assertions.assertEquals(tokenRetornado, tokenGerado);
        verify(logService).create(Descricao.LOGIN, usuarioValidado.getIdUsuario());
    }

    @Test
    public void testCreate() throws EntityNotFound, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = createUsuarioEntity();
        UsuarioInputDTO usuarioInput = createUsuarioInputDTO();
        UsuarioOutputDTO usuarioOutput = createUsuarioOutputDTO();
        UsuarioEntity usuarioRetornado = createUsuarioEntity();

        when(usuarioRepository.existsByLogin(usuarioInput.getLogin())).thenReturn(false);

        when(objectMapper.convertValue(usuarioInput, UsuarioEntity.class)).thenReturn(usuarioEntity);

        String senhaCriptografada = "senhaCriptografada";
        when(passwordEncoder.encode(usuarioInput.getSenha())).thenReturn(senhaCriptografada);

        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioRetornado);
        when(objectMapper.convertValue(usuarioEntity, UsuarioOutputDTO.class)).thenReturn(usuarioOutput);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        UsuarioOutputDTO usuarioCreated = usuarioService.create(usuarioInput);

        Assertions.assertNotNull(usuarioCreated);
        Assertions.assertEquals(usuarioEntity.getLogin(), usuarioCreated.getLogin());

        verify(logService).create(Descricao.CREATE, 1);
        verify(passwordEncoder).encode(usuarioInput.getSenha());
    }

    @Test
    public void testUpdate() throws EntityNotFound {

        Integer idUsuario = 1;
        UsuarioInputDTO usuarioInput = createUsuarioInputDTO();
        UsuarioOutputDTO usuarioOutput = createUsuarioOutputDTO();
        UsuarioEntity usuarioEntity = createUsuarioEntity();
        UsuarioEntity usuarioAtualizado = createUsuarioEntity();

        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioEntity));
        when(usuarioRepository.existsByLogin(usuarioInput.getLogin())).thenReturn(false);
        when(objectMapper.convertValue(usuarioInput, UsuarioEntity.class)).thenReturn(usuarioEntity);

        String senhaCriptografada = "senhaCriptografada";
        when(passwordEncoder.encode(usuarioInput.getSenha())).thenReturn(senhaCriptografada);

        when(usuarioRepository.save(usuarioEntity)).thenReturn(usuarioAtualizado);
        when(objectMapper.convertValue(usuarioEntity, UsuarioOutputDTO.class)).thenReturn(usuarioOutput);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("1");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        UsuarioOutputDTO usuarioUpdated = usuarioService.update(idUsuario, usuarioInput);

        Assertions.assertEquals(usuarioEntity.getLogin(), usuarioUpdated.getLogin());

        verify(usuarioRepository).save(usuarioEntity);
        verify(logService).create(Descricao.UPDATE, 1);
        verify(passwordEncoder).encode(usuarioInput.getSenha());
    }

    @Test
    public void testUpdatePassword() throws EntityNotFound, RegraDeNegocioException {
        Integer idUsuario = 1;

        UsuarioEntity usuarioEntity = createUsuarioEntity();
        when(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuarioEntity));

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

        UsuarioSenhaInputDTO usuarioSenhaInputDTO = new UsuarioSenhaInputDTO();
        usuarioSenhaInputDTO.setSenha("newPassword");
        usuarioService.updatePassword(usuarioSenhaInputDTO);

        Assertions.assertEquals(encodedPassword, usuarioEntity.getSenha(), "Encoded password should be set to the user entity");
        Assertions.assertEquals("newPassword", usuarioSenhaInputDTO.getSenha(), "Password in the input DTO should remain unchanged");

        verify(usuarioRepository).save(usuarioEntity);
        verify(logService).create(Descricao.UPDATE, idUsuario);
    }

    @Test
    public void testRemove() throws EntityNotFound {
        Integer idUsuario = 1;
        UsuarioEntity usuarioEntity = createUsuarioEntity();
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioEntity));

        usuarioService.remove(1);

        verify(usuarioRepository).delete(usuarioEntity);

        verify(logService).create(Descricao.DELETE, idUsuario);
    }

    @Test
    public void testGenerateRandomPassword() {
        String randomPassword = usuarioService.generateRandomPassword();

        Assertions.assertEquals(4, randomPassword.length(), "Generated password should have 4 digits");

        try {
            Integer.parseInt(randomPassword);
        } catch (NumberFormatException e) {
            Assertions.fail("Generated password should be a valid integer");
        }

        int generatedNumber = Integer.parseInt(randomPassword);
        Assertions.assertTrue(generatedNumber >= 1000
                && generatedNumber <= 9999, "Generated password should be between 1000 and 9999");
    }

    @Test
    public void testCriarUsuarioInput() {
        String login = "testUser";
        Integer cargo = 1;

        UsuarioInputDTO usuarioInput = usuarioService.criarUsuarioInput(login, cargo);

        Assertions.assertEquals(login, usuarioInput.getLogin(), "Generated login should match the input login");

        Assertions.assertNotNull(usuarioInput.getSenha(), "Generated password should not be null");
        Assertions.assertFalse(usuarioInput.getSenha().isEmpty(), "Generated password should not be empty");
        Assertions.assertTrue(usuarioInput.getCargos().contains(cargo), "Generated UsuarioInputDTO should contain the specified cargo");
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