package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoOutputDTO;
import br.com.dbc.wbhealth.model.dto.hospital.HospitalAtendimentoDTO;
import br.com.dbc.wbhealth.model.dto.hospital.HospitalInputDTO;
import br.com.dbc.wbhealth.model.dto.hospital.HospitalOutputDTO;
import br.com.dbc.wbhealth.model.entity.AtendimentoEntity;
import br.com.dbc.wbhealth.model.entity.HospitalEntity;
import br.com.dbc.wbhealth.model.entity.MedicoEntity;
import br.com.dbc.wbhealth.model.entity.PacienteEntity;
import br.com.dbc.wbhealth.model.enumarator.TipoDeAtendimento;
import br.com.dbc.wbhealth.repository.HospitalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.result.Output;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HospitalServiceTest {
    @InjectMocks
    private HospitalService hospitalService;
    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private ObjectMapper objectMapper;

    private HospitalEntity hospitalSimulado;
    private HospitalOutputDTO hospitalOutputSimulado;

    @BeforeEach
    public void setUp(){
        hospitalSimulado = new HospitalEntity();
        hospitalSimulado.setIdHospital(1);
        hospitalSimulado.setNome("Hospital Santa Maria");

        hospitalOutputSimulado = new HospitalOutputDTO();
        hospitalOutputSimulado.setIdHospital(1);
        hospitalOutputSimulado.setNome("Hospital Santa Maria");
    }

    @AfterEach
    public void tearDown(){
        hospitalSimulado = null;
        hospitalOutputSimulado = null;
    }

    @Test
    void testFindAll() {
        List<HospitalEntity> hospitais = new ArrayList<>();
        hospitais.add(hospitalSimulado);

        List<HospitalOutputDTO> hospitaisOutput = new ArrayList<>();
        hospitaisOutput.add(hospitalOutputSimulado);

        when(hospitalRepository.findAll()).thenReturn(hospitais);
        when(objectMapper.convertValue(hospitalSimulado, HospitalOutputDTO.class)).thenReturn(hospitalOutputSimulado);

        List<HospitalOutputDTO> resultado = hospitalService.findAll();

        verify(hospitalRepository).findAll();
        verify(objectMapper).convertValue(hospitalSimulado, HospitalOutputDTO.class);

        assertNotNull(resultado);
        assertEquals(hospitaisOutput, resultado);
    }

    @Test
    void testFindById() throws EntityNotFound {
        when(hospitalRepository.findById(hospitalSimulado.getIdHospital())).thenReturn(Optional.of(hospitalSimulado));
        when(objectMapper.convertValue(hospitalSimulado, HospitalOutputDTO.class)).thenReturn(hospitalOutputSimulado);

        HospitalOutputDTO resultado = hospitalService.findById(hospitalSimulado.getIdHospital());

        verify(hospitalRepository).findById(hospitalSimulado.getIdHospital());
        verify(objectMapper).convertValue(hospitalSimulado, HospitalOutputDTO.class);

        assertNotNull(resultado);
        assertDoesNotThrow(() -> new EntityNotFound("Hospital não encontrado"));
        assertEquals(hospitalOutputSimulado, resultado);
    }

    @Test
    void testSave() {
        HospitalInputDTO hospitalInput = new HospitalInputDTO("Hospital Santa Maria");
        HospitalEntity hospitalSemId = new HospitalEntity();
        hospitalSemId.setNome(hospitalInput.getNome());

        when(objectMapper.convertValue(hospitalInput, HospitalEntity.class)).thenReturn(hospitalSemId);
        when(hospitalRepository.save(hospitalSemId)).thenReturn(hospitalSimulado);
        when(objectMapper.convertValue(hospitalSimulado, HospitalOutputDTO.class)).thenReturn(hospitalOutputSimulado);

        HospitalOutputDTO resultado = hospitalService.save(hospitalInput);

        verify(objectMapper).convertValue(hospitalInput, HospitalEntity.class);
        verify(hospitalRepository).save(hospitalSemId);
        verify(objectMapper).convertValue(hospitalSimulado, HospitalOutputDTO.class);

        assertNotNull(resultado);
        assertEquals(hospitalOutputSimulado, resultado);
    }

    @Test
    void testUpdate() throws EntityNotFound {
        HospitalInputDTO hospitalInput = new HospitalInputDTO("Hospital Santa Rita");

        HospitalEntity hospitalAtualizado = new HospitalEntity();
        hospitalAtualizado.setIdHospital(1);
        hospitalAtualizado.setNome("Hospital Santa Rita");

        hospitalOutputSimulado.setNome("Hospital Santa Rita");

        when(hospitalRepository.findById(hospitalSimulado.getIdHospital())).thenReturn(Optional.of(hospitalSimulado));
        when(hospitalRepository.save(hospitalSimulado)).thenReturn(hospitalAtualizado);
        when(objectMapper.convertValue(hospitalSimulado, HospitalOutputDTO.class)).thenReturn(hospitalOutputSimulado);

        HospitalOutputDTO resultado = hospitalService.update(hospitalSimulado.getIdHospital(), hospitalInput);

        verify(hospitalRepository).findById(hospitalSimulado.getIdHospital());
        verify(hospitalRepository).save(hospitalSimulado);
        verify(objectMapper).convertValue(hospitalSimulado, HospitalOutputDTO.class);

        assertNotNull(resultado);
        assertDoesNotThrow(() -> new EntityNotFound("Hospital não encontrado"));
        assertEquals(hospitalOutputSimulado, resultado);
    }

    @Test
    void testDeleteById() throws EntityNotFound {
        when(hospitalRepository.findById(hospitalSimulado.getIdHospital())).thenReturn(Optional.of(hospitalSimulado));

        hospitalService.deleteById(hospitalSimulado.getIdHospital());

        verify(hospitalRepository).findById(hospitalSimulado.getIdHospital());
        verify(hospitalRepository).delete(hospitalSimulado);

        assertDoesNotThrow(() -> new EntityNotFound("Hospital não encontrado"));
    }

    @Test
    void testFindHospitaisWithAllAtendimentos() {
        Pageable paginacaoSimulada = PageRequest.of(0, 5, Sort.by("idHospital"));
        Page<HospitalEntity> hospitaisPaginados = getHospitaisPaginados(paginacaoSimulada);
        Page<HospitalAtendimentoDTO> hospitaisPaginadosOutput = getHospitaisPaginadosOutput(paginacaoSimulada);

        when(hospitalRepository.findAll(paginacaoSimulada)).thenReturn(hospitaisPaginados);

        Page<HospitalAtendimentoDTO> resultado = hospitalService.findHospitaisWithAllAtendimentos(0, 5);

        verify(hospitalRepository).findAll(paginacaoSimulada);

        assertNotNull(resultado);
        assertEquals(hospitaisPaginadosOutput, resultado);
    }

    private Page<HospitalEntity> getHospitaisPaginados(Pageable paginacao){
        HospitalEntity hospitalComAtendimentos = new HospitalEntity();
        AtendimentoEntity atendimentoSimulado = getAtendimentoEntity(hospitalComAtendimentos);

        hospitalComAtendimentos.setIdHospital(1);
        hospitalComAtendimentos.setNome("Hospital Santa Maria");
        hospitalComAtendimentos.setAtendimentos(new HashSet<>(List.of(atendimentoSimulado)));

        List<HospitalEntity> hospitais = List.of(hospitalComAtendimentos);

        return new PageImpl<>(hospitais, paginacao, hospitais.size());
    }

    private Page<HospitalAtendimentoDTO> getHospitaisPaginadosOutput(Pageable paginacao){
        HospitalAtendimentoDTO hospitalAtendimentoOutput = new HospitalAtendimentoDTO();
        AtendimentoOutputDTO atendimentoOutput = getAtendimentoOutput();

        hospitalAtendimentoOutput.setIdHospital(1);
        hospitalAtendimentoOutput.setNome("Hospital Santa Maria");
        hospitalAtendimentoOutput.setAtendimentos(List.of(atendimentoOutput));

        List<HospitalAtendimentoDTO> hospitais = List.of(hospitalAtendimentoOutput);

        return new PageImpl<>(hospitais, paginacao, hospitais.size());
    }

    private AtendimentoEntity getAtendimentoEntity(HospitalEntity hospital){
        AtendimentoEntity atendimento = new AtendimentoEntity();

        atendimento.setIdAtendimento(1);
        atendimento.setHospitalEntity(hospital);
        atendimento.setPacienteEntity(new PacienteEntity());
        atendimento.setMedicoEntity(new MedicoEntity());
        atendimento.setDataAtendimento(LocalDate.now());
        atendimento.setLaudo("Dor de cabeça");
        atendimento.setTipoDeAtendimento(TipoDeAtendimento.CONSULTA);
        atendimento.setValorDoAtendimento(200.0);

        atendimento.getPacienteEntity().setIdPaciente(1);
        atendimento.getMedicoEntity().setIdMedico(1);
        return atendimento;
    }

    private AtendimentoOutputDTO getAtendimentoOutput(){
        AtendimentoOutputDTO atendimentoOutput = new AtendimentoOutputDTO();

        atendimentoOutput.setIdAtendimento(1);
        atendimentoOutput.setIdHospital(1);
        atendimentoOutput.setIdPaciente(1);
        atendimentoOutput.setIdMedico(1);
        atendimentoOutput.setDataAtendimento(LocalDate.now());
        atendimentoOutput.setLaudo("Dor de cabeça");
        atendimentoOutput.setTipoDeAtendimento(TipoDeAtendimento.CONSULTA.name());
        atendimentoOutput.setValorDoAtendimento(200.0);

        return atendimentoOutput;
    }

}