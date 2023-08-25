package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoInputDTO;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoOutputDTO;
import br.com.dbc.wbhealth.model.entity.AtendimentoEntity;
import br.com.dbc.wbhealth.model.entity.HospitalEntity;
import br.com.dbc.wbhealth.model.entity.MedicoEntity;
import br.com.dbc.wbhealth.model.entity.PacienteEntity;
import br.com.dbc.wbhealth.model.enumarator.TipoDeAtendimento;
import br.com.dbc.wbhealth.repository.AtendimentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class AtendimentoServiceTest {
    @InjectMocks
    private AtendimentoService atendimentoService;
    @Mock
    private AtendimentoRepository atendimentoRepository;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PacienteService pacienteService;
    @Mock
    private MedicoService medicoService;
    @Mock
    private HospitalService hospitalService;

    MedicoEntity medicoEntity = new MedicoEntity();
    PacienteEntity pacienteEntity = new PacienteEntity();
    HospitalEntity hospitalEntity = new HospitalEntity();

    AtendimentoEntity atendimento1 = new AtendimentoEntity();
    AtendimentoEntity atendimento2 = new AtendimentoEntity();
    AtendimentoEntity atendimento3 = new AtendimentoEntity();


    @BeforeEach
    private void criarEntidadesDeApoio() {
        medicoEntity.setIdMedico(1);
        pacienteEntity.setIdPaciente(4);
        hospitalEntity.setIdHospital(2);

        atendimento1.setIdAtendimento(1);
        atendimento1.setHospitalEntity(hospitalEntity);
        atendimento1.setPacienteEntity(pacienteEntity);
        atendimento1.setMedicoEntity(medicoEntity);
        atendimento1.setDataAtendimento(LocalDate.now());
        atendimento1.setLaudo("Câncer");
        atendimento1.setTipoDeAtendimento(TipoDeAtendimento.CONSULTA);
        atendimento1.setReceita("Quimioterapia");
        atendimento1.setValorDoAtendimento(100.0);

        atendimento2.setIdAtendimento(2);
        atendimento2.setHospitalEntity(hospitalEntity);
        atendimento2.setPacienteEntity(pacienteEntity);
        atendimento2.setMedicoEntity(medicoEntity);
        atendimento2.setDataAtendimento(LocalDate.now());
        atendimento2.setLaudo("Tétano");
        atendimento2.setTipoDeAtendimento(TipoDeAtendimento.EXAME);
        atendimento2.setReceita("Repouso");
        atendimento2.setValorDoAtendimento(200.0);

        atendimento3.setIdAtendimento(3);
        atendimento3.setHospitalEntity(hospitalEntity);
        atendimento3.setPacienteEntity(pacienteEntity);
        atendimento3.setMedicoEntity(medicoEntity);
        atendimento3.setDataAtendimento(LocalDate.now());
        atendimento3.setLaudo("Sifilis");
        atendimento3.setTipoDeAtendimento(TipoDeAtendimento.CIRURGIA);
        atendimento3.setReceita("Novalgina");
        atendimento3.setValorDoAtendimento(300.0);
    }


    @Test
    public void testSave() throws EntityNotFound {
        // Arrange
        AtendimentoInputDTO atendimentoInput = new AtendimentoInputDTO();
        atendimentoInput.setIdHospital(hospitalEntity.getIdHospital());
        atendimentoInput.setIdPaciente(pacienteEntity.getIdPaciente());
        atendimentoInput.setIdMedico(medicoEntity.getIdMedico());
        atendimentoInput.setDataAtendimento(LocalDate.now());
        atendimentoInput.setLaudo("Varizes");
        atendimentoInput.setTipoDeAtendimento("CONSULTA");
        atendimentoInput.setReceita("Dorflex");
        atendimentoInput.setValorDoAtendimento(500.0);

        AtendimentoEntity atendimentoEntity = new AtendimentoEntity();
        BeanUtils.copyProperties(atendimentoInput, atendimentoEntity);
        atendimentoEntity.setIdAtendimento(1);
        atendimentoEntity.setMedicoEntity(medicoEntity);
        atendimentoEntity.setPacienteEntity(pacienteEntity);
        atendimentoEntity.setHospitalEntity(hospitalEntity);

        AtendimentoOutputDTO atendimentoOutput = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimentoEntity, atendimentoOutput);

        when(atendimentoRepository.save(any())).thenReturn(atendimentoEntity);
        when(objectMapper.convertValue(atendimentoEntity, AtendimentoOutputDTO.class)).thenReturn(atendimentoOutput);

        atendimentoOutput.setIdHospital(atendimentoEntity.getHospitalEntity().getIdHospital());
        atendimentoOutput.setIdMedico(atendimentoEntity.getMedicoEntity().getIdMedico());
        atendimentoOutput.setIdPaciente(atendimentoEntity.getPacienteEntity().getIdPaciente());

        // Act
        AtendimentoOutputDTO result = atendimentoService.save(atendimentoInput);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(atendimentoOutput, result);
    }

    @Test
    public void testFindAll() {
        // Arrange
        List<AtendimentoEntity> atendimentoEntityList = new ArrayList<>();
        atendimentoEntityList.add(atendimento1);
        atendimentoEntityList.add(atendimento2);

        AtendimentoOutputDTO atendimentoOutputDTO1 = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimento1, atendimentoOutputDTO1);

        AtendimentoOutputDTO atendimentoOutputDTO2 = new AtendimentoOutputDTO();
        BeanUtils.copyProperties(atendimento2, atendimentoOutputDTO2);

        List<AtendimentoOutputDTO> atendimentoOutputList = new ArrayList<>();
        atendimentoOutputList.add(atendimentoOutputDTO1);
        atendimentoOutputList.add(atendimentoOutputDTO2);


        when(atendimentoRepository.findAll()).thenReturn(atendimentoEntityList);
        when(objectMapper.convertValue(atendimento1, AtendimentoOutputDTO.class)).thenReturn(atendimentoOutputDTO1);
        atendimentoOutputDTO1.setIdHospital(atendimento1.getHospitalEntity().getIdHospital());
        atendimentoOutputDTO1.setIdMedico(atendimento1.getMedicoEntity().getIdMedico());
        atendimentoOutputDTO1.setIdPaciente(atendimento1.getPacienteEntity().getIdPaciente());
        when(objectMapper.convertValue(atendimento2, AtendimentoOutputDTO.class)).thenReturn(atendimentoOutputDTO2);
        atendimentoOutputDTO2.setIdHospital(atendimento2.getHospitalEntity().getIdHospital());
        atendimentoOutputDTO2.setIdMedico(atendimento2.getMedicoEntity().getIdMedico());
        atendimentoOutputDTO2.setIdPaciente(atendimento2.getPacienteEntity().getIdPaciente());

        // Act
        List<AtendimentoOutputDTO> result = atendimentoService.findAll();

        //Assert
        Assertions.assertEquals(atendimentoOutputList, result);
    }
}
