package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.entity.CargoEntity;
import br.com.dbc.wbhealth.model.entity.UsuarioEntity;
import br.com.dbc.wbhealth.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    UsuarioService usuarioService;
    @Mock
    UsuarioRepository usuarioRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    CargoService cargoService;
    @Mock
    ObjectMapper objectMapper;

    UsuarioEntity usuario1 = new UsuarioEntity();
    UsuarioEntity usuario2 = new UsuarioEntity();
    CargoEntity cargo1 = new CargoEntity();
    CargoEntity cargo2 = new CargoEntity();

    @BeforeEach
    private void criarEntidadesDeApoio() {
        cargo1.setIdCargo(4);
        cargo1.setNome("MEDICO");
        Set<CargoEntity> cargos1 = new HashSet<>();

        usuario1.setIdUsuario(1);
        usuario1.setLogin("maria");
        usuario1.setSenha("1234");
        usuario1.setCargos(cargos1);
        cargos1.add(cargo1);
    }

    @Test
    public void testFindById() {


        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.ofNullable(usuario1));

        Assertions.assertEquals(1, usuario1.getIdUsuario());
        Assertions.assertEquals("maria", usuario1.getLogin());
        Assertions.assertEquals("1234", usuario1.getPassword());

    }
}