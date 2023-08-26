package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoInputDTO;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoOutputDTO;
import br.com.dbc.wbhealth.model.dto.paciente.PacienteOutputDTO;
import br.com.dbc.wbhealth.model.entity.AtendimentoEntity;
import br.com.dbc.wbhealth.model.entity.HospitalEntity;
import br.com.dbc.wbhealth.model.entity.MedicoEntity;
import br.com.dbc.wbhealth.model.entity.PacienteEntity;
import br.com.dbc.wbhealth.model.enumarator.TipoDeAtendimento;
import br.com.dbc.wbhealth.repository.AtendimentoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AtendimentoServiceTest {
    @InjectMocks
    private AtendimentoService atendimentoService;
    @Mock
    private AtendimentoRepository atendimentoRepository;
    @Mock
    private PacienteService pacienteService;
    @Mock
    private MedicoService medicoService;
    @Mock
    private HospitalService hospitalService;

    MedicoEntity medicoEntity1 = new MedicoEntity();
    PacienteEntity pacienteEntity1 = new PacienteEntity();
    HospitalEntity hospitalEntity1 = new HospitalEntity();

    MedicoEntity medicoEntity2 = new MedicoEntity();
    PacienteEntity pacienteEntity2 = new PacienteEntity();
    HospitalEntity hospitalEntity2 = new HospitalEntity();

    PacienteOutputDTO pacienteOutputDTO1 = new PacienteOutputDTO();

    AtendimentoEntity atendimentoEntity1 = new AtendimentoEntity();

    AtendimentoEntity atendimentoEntity2 = new AtendimentoEntity();

    AtendimentoOutputDTO atendimentoOutputDTO1 = new AtendimentoOutputDTO();

    AtendimentoOutputDTO atendimentoOutputDTO2 = new AtendimentoOutputDTO();


    @BeforeEach
    private void criarEntidadesDeApoio() {
        medicoEntity1.setIdMedico(1);
        pacienteEntity1.setIdPaciente(4);
        pacienteOutputDTO1.setIdPaciente(4);
        hospitalEntity1.setIdHospital(2);

        medicoEntity2.setIdMedico(2);
        pacienteEntity2.setIdPaciente(5);
        hospitalEntity2.setIdHospital(2);

        atendimentoEntity1.setIdAtendimento(1);
        atendimentoEntity1.setHospitalEntity(hospitalEntity1);
        atendimentoEntity1.setPacienteEntity(pacienteEntity1);
        atendimentoEntity1.setMedicoEntity(medicoEntity1);
        atendimentoEntity1.setDataAtendimento(LocalDate.now());
        atendimentoEntity1.setLaudo("Câncer");
        atendimentoEntity1.setTipoDeAtendimento(TipoDeAtendimento.CONSULTA);
        atendimentoEntity1.setReceita("Quimioterapia");
        atendimentoEntity1.setValorDoAtendimento(100.0);

        atendimentoEntity2.setIdAtendimento(2);
        atendimentoEntity2.setHospitalEntity(hospitalEntity2);
        atendimentoEntity2.setPacienteEntity(pacienteEntity2);
        atendimentoEntity2.setMedicoEntity(medicoEntity2);
        atendimentoEntity2.setDataAtendimento(LocalDate.now());
        atendimentoEntity2.setLaudo("Gripe");
        atendimentoEntity2.setTipoDeAtendimento(TipoDeAtendimento.CONSULTA);
        atendimentoEntity2.setReceita("Dorflex");
        atendimentoEntity2.setValorDoAtendimento(400.0);

        BeanUtils.copyProperties(atendimentoEntity1, atendimentoOutputDTO1);
        atendimentoOutputDTO1.setTipoDeAtendimento(atendimentoEntity1.getTipoDeAtendimento().name());
        atendimentoOutputDTO1.setIdHospital(atendimentoEntity1.getHospitalEntity().getIdHospital());
        atendimentoOutputDTO1.setIdPaciente(atendimentoEntity1.getPacienteEntity().getIdPaciente());
        atendimentoOutputDTO1.setIdMedico(atendimentoEntity1.getMedicoEntity().getIdMedico());

        BeanUtils.copyProperties(atendimentoEntity2, atendimentoOutputDTO2);
        atendimentoOutputDTO2.setTipoDeAtendimento(atendimentoEntity2.getTipoDeAtendimento().name());
        atendimentoOutputDTO2.setIdHospital(atendimentoEntity2.getHospitalEntity().getIdHospital());
        atendimentoOutputDTO2.setIdPaciente(atendimentoEntity2.getPacienteEntity().getIdPaciente());
        atendimentoOutputDTO2.setIdMedico(atendimentoEntity2.getMedicoEntity().getIdMedico());
    }

    @AfterEach
    public void tearDown() {
        medicoEntity1 = null;
        pacienteEntity1 = null;
        hospitalEntity1 = null;

        medicoEntity2 = null;
        pacienteEntity2 = null;
        hospitalEntity2 = null;

        pacienteOutputDTO1 = null;

        atendimentoEntity1 = null;
        atendimentoEntity2 = null;
        atendimentoOutputDTO1 = null;
        atendimentoOutputDTO2 = null;

    }


    @org.junit.jupiter.api.Test
    public void testSave() throws EntityNotFound {
        // Arrange
        AtendimentoInputDTO atendimentoInput = new AtendimentoInputDTO();
        atendimentoInput.setIdHospital(hospitalEntity1.getIdHospital());
        atendimentoInput.setIdPaciente(pacienteEntity1.getIdPaciente());
        atendimentoInput.setIdMedico(medicoEntity1.getIdMedico());
        atendimentoInput.setDataAtendimento(LocalDate.now());
        atendimentoInput.setLaudo("Varizes");
        atendimentoInput.setTipoDeAtendimento("CONSULTA");
        atendimentoInput.setReceita("Dorflex");
        atendimentoInput.setValorDoAtendimento(500.0);

        BeanUtils.copyProperties(atendimentoInput, atendimentoEntity1);
        atendimentoEntity1.setIdAtendimento(1);
        atendimentoEntity1.setMedicoEntity(medicoEntity1);
        atendimentoEntity1.setPacienteEntity(pacienteEntity1);
        atendimentoEntity1.setHospitalEntity(hospitalEntity1);

        AtendimentoOutputDTO atendimentoOutput = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimentoEntity1, atendimentoOutput);
        atendimentoOutput.setTipoDeAtendimento(atendimentoEntity1.getTipoDeAtendimento().name());
        atendimentoOutput.setIdHospital(atendimentoEntity1.getHospitalEntity().getIdHospital());
        atendimentoOutput.setIdMedico(atendimentoEntity1.getMedicoEntity().getIdMedico());
        atendimentoOutput.setIdPaciente(atendimentoEntity1.getPacienteEntity().getIdPaciente());

        when(atendimentoRepository.save(any())).thenReturn(atendimentoEntity1);

        // Act
        AtendimentoOutputDTO result = atendimentoService.save(atendimentoInput);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(atendimentoOutput, result);
    }


    @Test
    public void testFindAll() {
        // Arrange
        when(atendimentoRepository.findAll()).thenReturn(Collections.singletonList(atendimentoEntity1));

        // Act
        List<AtendimentoOutputDTO> result = atendimentoService.findAll();

        //Assert
        Assertions.assertIterableEquals(Collections.singletonList(atendimentoOutputDTO1), result);
    }

    @Test
    public void testFindById() throws EntityNotFound {
        // Arrange
        Integer idAtendimento = 1;

        when(atendimentoRepository.findById(idAtendimento)).thenReturn(Optional.of(atendimentoEntity1));

        // Act
        AtendimentoOutputDTO result = atendimentoService.findById(idAtendimento);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(atendimentoOutputDTO1, result);
    }

    @Test
    public void testUpdate() throws EntityNotFound {
        // Arrange
        Integer idAtendimento = 1;
        AtendimentoInputDTO atendimentoInput = new AtendimentoInputDTO();
        atendimentoInput.setIdHospital(hospitalEntity1.getIdHospital());
        atendimentoInput.setIdPaciente(pacienteEntity1.getIdPaciente());
        atendimentoInput.setIdMedico(medicoEntity1.getIdMedico());
        atendimentoInput.setDataAtendimento(LocalDate.now());
        atendimentoInput.setLaudo("Varizes");
        atendimentoInput.setTipoDeAtendimento("CONSULTA");
        atendimentoInput.setReceita("Quimioterapia");
        atendimentoInput.setValorDoAtendimento(100.0);

        AtendimentoOutputDTO atendimentoOutputDTOIntermediario = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimentoEntity1, atendimentoOutputDTOIntermediario);
        atendimentoOutputDTOIntermediario.setTipoDeAtendimento(atendimentoEntity1.getTipoDeAtendimento().name());

        AtendimentoEntity atendimentoEntityAtualizado = new AtendimentoEntity();
        BeanUtils.copyProperties(atendimentoEntity1, atendimentoEntityAtualizado);
        atendimentoEntityAtualizado.setLaudo("Varizes");

        AtendimentoOutputDTO atendimentoOutputDTOFinal = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimentoEntityAtualizado, atendimentoOutputDTOFinal);
        atendimentoOutputDTOFinal.setTipoDeAtendimento(atendimentoEntityAtualizado.getTipoDeAtendimento().name());

        when(atendimentoRepository.findById(idAtendimento)).thenReturn(Optional.of(atendimentoEntity1));

        atendimentoOutputDTOIntermediario.setIdHospital(atendimentoEntity1.getHospitalEntity().getIdHospital());
        atendimentoOutputDTOIntermediario.setIdMedico(atendimentoEntity1.getMedicoEntity().getIdMedico());
        atendimentoOutputDTOIntermediario.setIdPaciente(atendimentoEntity1.getPacienteEntity().getIdPaciente());

        when(atendimentoRepository.save(any())).thenReturn(atendimentoEntityAtualizado);

        atendimentoOutputDTOFinal.setIdHospital(atendimentoEntity1.getHospitalEntity().getIdHospital());
        atendimentoOutputDTOFinal.setIdMedico(atendimentoEntity1.getMedicoEntity().getIdMedico());
        atendimentoOutputDTOFinal.setIdPaciente(atendimentoEntity1.getPacienteEntity().getIdPaciente());

        // Act
        AtendimentoOutputDTO result = atendimentoService.update(idAtendimento, atendimentoInput);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(atendimentoOutputDTOFinal, result);
    }

    @Test
    public void testBuscarAtendimentoPeloIdPaciente() throws EntityNotFound {
        Integer idPaciente = 4;

        List<AtendimentoOutputDTO> atendimentoOutputListAntesDeFiltrar = new ArrayList<>();
        atendimentoOutputListAntesDeFiltrar.add(atendimentoOutputDTO1);
        atendimentoOutputListAntesDeFiltrar.add(atendimentoOutputDTO2);

        List<AtendimentoOutputDTO> atendimentoOutputListDepoisDeFiltrar = new ArrayList<>();
        atendimentoOutputListDepoisDeFiltrar.add(atendimentoOutputDTO1);

        when(atendimentoRepository.findAll()).thenReturn(Collections.singletonList(atendimentoEntity1));
        when(pacienteService.findById(idPaciente)).thenReturn(pacienteOutputDTO1);

        List<AtendimentoOutputDTO> result = atendimentoService.bucarAtendimentoPeloIdPaciente(idPaciente);

        Assertions.assertEquals(atendimentoOutputListDepoisDeFiltrar.size(), result.size());
        Assertions.assertIterableEquals(atendimentoOutputListDepoisDeFiltrar, result);

    }

    @Test
    public void testDeletarPeloId() throws EntityNotFound {
        Integer idAtendimento = 1;

        when(atendimentoRepository.findById(idAtendimento)).thenReturn(Optional.of(atendimentoEntity1));

        doNothing().when(atendimentoRepository).deleteById(idAtendimento);

        // Act
        atendimentoService.deletarPeloId(idAtendimento);

        // Assert
        verify(atendimentoRepository, times(1)).findById(idAtendimento);
        verify(atendimentoRepository, times(1)).deleteById(idAtendimento);
    }

}
