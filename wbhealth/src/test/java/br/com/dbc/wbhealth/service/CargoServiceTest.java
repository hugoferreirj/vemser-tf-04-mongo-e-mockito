package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.entity.CargoEntity;
import br.com.dbc.wbhealth.model.entity.UsuarioEntity;
import br.com.dbc.wbhealth.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {
    @InjectMocks
    private CargoService cargoService;
    @Mock
    private CargoRepository cargoRepository;

    @Test
    public void testFindById() throws EntityNotFound {
        // Arrange
        CargoEntity cargoTeste = new CargoEntity();
        UsuarioEntity usuarioTeste = new UsuarioEntity();

        cargoTeste.setIdCargo(1);
        cargoTeste.setNome("ROLE_ADMIN");
        cargoTeste.setUsuarios(new HashSet<>());
        cargoTeste.getUsuarios().add(usuarioTeste);

        usuarioTeste.setIdUsuario(1);
        usuarioTeste.setLogin("admin");
        usuarioTeste.setSenha("admin12345");
        usuarioTeste.setCargos(new HashSet<>());
        usuarioTeste.getCargos().add(cargoTeste);

        // Act
        when(cargoRepository.findById(cargoTeste.getIdCargo())).thenReturn(Optional.of(cargoTeste));
        CargoEntity cargoEncontrado = cargoService.findById(cargoTeste.getIdCargo());

        // Assert
        verify(cargoRepository).findById(cargoTeste.getIdCargo());
        assertNotNull(cargoEncontrado);
        assertDoesNotThrow(() -> new EntityNotFound("Cargo não encontrado: " + cargoTeste.getIdCargo()));
        assertEquals(cargoTeste, cargoEncontrado);
    }

}