package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.BancoDeDadosException;
import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.atendimento.AtendimentoOutputDTO;
import br.com.dbc.wbhealth.model.dto.paciente.PacienteAtendimentosOutputDTO;
import br.com.dbc.wbhealth.model.dto.paciente.PacienteInputDTO;
import br.com.dbc.wbhealth.model.dto.paciente.PacienteNovoOutputDTO;
import br.com.dbc.wbhealth.model.dto.paciente.PacienteOutputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioOutputDTO;
import br.com.dbc.wbhealth.model.entity.*;
import br.com.dbc.wbhealth.model.enumarator.TipoDeAtendimento;
import br.com.dbc.wbhealth.repository.PacienteRepository;
import br.com.dbc.wbhealth.repository.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {
    @InjectMocks
    private PacienteService pacienteService;
    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private PessoaRepository pessoaRepository;
    @Mock
    private HospitalService hospitalService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private EmailService emailService;
    @Mock
    private ObjectMapper objectMapper;

    private PacienteEntity pacienteSimulado;
    private PacienteOutputDTO pacienteOutputSimulado;
    private AtendimentoEntity atendimentoSimulado;

    @BeforeEach
    public void setUp(){
        pacienteSimulado = getPacienteSimulado();
        pacienteOutputSimulado = getPacienteOutputSimulado();
        atendimentoSimulado = getAtendimentoDoPacienteSimulado(pacienteSimulado);
    }

    @AfterEach
    public void tearDown(){
        pacienteSimulado = null;
        pacienteOutputSimulado = null;
        atendimentoSimulado = null;
    }

    @Test
    void testFindAll() {
        Pageable paginacao = PageRequest.of(0, 5, Sort.by("idPaciente"));
        Page<PacienteEntity> pacientesPaginados = getPacientesPaginados(pacienteSimulado, paginacao);
        Page<PacienteOutputDTO> pacientesPaginadosOutput = getPacientesPaginados(pacienteOutputSimulado, paginacao);

        when(pacienteRepository.findAll(paginacao)).thenReturn(pacientesPaginados);
        when(objectMapper.convertValue(pacienteSimulado, PacienteOutputDTO.class)).thenReturn(pacienteOutputSimulado);

        Page<PacienteOutputDTO> resultado = pacienteService.findAll(0, 5);

        verify(pacienteRepository).findAll(paginacao);
        verify(objectMapper).convertValue(pacienteSimulado, PacienteOutputDTO.class);

        assertNotNull(resultado);
        assertEquals(pacientesPaginadosOutput, resultado);
    }

    @Test
    void testFindAllAtendimentos() {
        PacienteAtendimentosOutputDTO pacienteAtendimentosOutputSimulado = getPacienteAtendimentosOutputSimulado();
        AtendimentoOutputDTO atendimentoOutput = pacienteAtendimentosOutputSimulado.getAtendimentos().stream().toList().get(0);

        Pageable paginacao = PageRequest.of(0, 5, Sort.by("idPaciente"));
        Page<PacienteEntity> pacientesPaginados = getPacientesPaginados(pacienteSimulado, paginacao);
        Page<PacienteAtendimentosOutputDTO> pacientesPaginadosOutput = getPacientesPaginados(
                pacienteAtendimentosOutputSimulado, paginacao
        );

        when(pacienteRepository.findAll(paginacao)).thenReturn(pacientesPaginados);
        when(objectMapper.convertValue(
                pacienteSimulado, PacienteAtendimentosOutputDTO.class
        )).thenReturn(pacienteAtendimentosOutputSimulado);
        when(objectMapper.convertValue(
                atendimentoSimulado, AtendimentoOutputDTO.class
        )).thenReturn(atendimentoOutput);

        Page<PacienteAtendimentosOutputDTO> resultadoPaginado = pacienteService.findAllAtendimentos(0, 5);

        verify(pacienteRepository).findAll(paginacao);
        verify(objectMapper).convertValue(pacienteSimulado, PacienteAtendimentosOutputDTO.class);
        verify(objectMapper).convertValue(atendimentoSimulado, AtendimentoOutputDTO.class);

        assertNotNull(resultadoPaginado);
        assertEquals(pacientesPaginadosOutput, resultadoPaginado);
    }

    @Test
    void testFindById() throws EntityNotFound {
        when(pacienteRepository.findById(pacienteSimulado.getIdPaciente())).thenReturn(Optional.of(pacienteSimulado));
        when(objectMapper.convertValue(pacienteSimulado, PacienteOutputDTO.class)).thenReturn(pacienteOutputSimulado);

        PacienteOutputDTO resultado = pacienteService.findById(pacienteSimulado.getIdPaciente());

        verify(pacienteRepository).findById(pacienteSimulado.getIdPaciente());
        verify(objectMapper).convertValue(pacienteSimulado, PacienteOutputDTO.class);

        assertDoesNotThrow(() -> new EntityNotFound("Paciente não encontrado"));
        assertNotNull(resultado);
        assertEquals(pacienteOutputSimulado, resultado);
    }

    @Test
    void testSave() throws EntityNotFound, BancoDeDadosException, MessagingException {
        PessoaEntity pessoaSimulada = pacienteSimulado.getPessoa();
        HospitalEntity hospitalSimulado = pacienteSimulado.getHospitalEntity();

        PacienteInputDTO pacienteInputSimulado = getPacienteInputSimulado();
        UsuarioInputDTO usuarioInputSimulado = getUsuarioInputSimulado();
        UsuarioOutputDTO usuarioOutputSimulado = getUsuarioOutputSimulado();
        PacienteNovoOutputDTO pacienteNovoOutputSimulado = getPacienteNovoOutputSimulado();

        when(objectMapper.convertValue(pacienteInputSimulado, PessoaEntity.class)).thenReturn(pessoaSimulada);
        when(usuarioService.criarUsuarioInput(pessoaSimulada.getCpf(), 3)).thenReturn(usuarioInputSimulado);
        when(usuarioService.create(usuarioInputSimulado)).thenReturn(usuarioOutputSimulado);
        when(pessoaRepository.save(pessoaSimulada)).thenReturn(pessoaSimulada);
        when(hospitalService.getHospitalById(hospitalSimulado.getIdHospital())).thenReturn(hospitalSimulado);
        when(objectMapper.convertValue(pacienteInputSimulado, PacienteEntity.class)).thenReturn(pacienteSimulado);
        when(pacienteRepository.save(pacienteSimulado)).thenReturn(pacienteSimulado);
        when(objectMapper.convertValue(pacienteSimulado, PacienteNovoOutputDTO.class)).thenReturn(pacienteNovoOutputSimulado);

        PacienteNovoOutputDTO resultado = pacienteService.save(pacienteInputSimulado);

        verify(usuarioService).criarUsuarioInput(pessoaSimulada.getCpf(), 3);
        verify(usuarioService).create(usuarioInputSimulado);
        verify(pessoaRepository).save(pessoaSimulada);
        verify(hospitalService).getHospitalById(hospitalSimulado.getIdHospital());
        verify(pacienteRepository).save(pacienteSimulado);
        verify(objectMapper).convertValue(pacienteSimulado, PacienteNovoOutputDTO.class);

        assertDoesNotThrow(() -> new BancoDeDadosException("CPF já cadastrado."));
        assertNotNull(resultado);
        assertEquals(pacienteNovoOutputSimulado, resultado);
    }

    @Test
    void testSaveThrowsBancoDeDadosException() {
        PacienteInputDTO pacienteInputSimulado = getPacienteInputSimulado();
        String mensagemEsperada = "CPF já cadastrado.";

        when(objectMapper.convertValue(pacienteInputSimulado, PessoaEntity.class)).thenReturn(pacienteSimulado.getPessoa());
        when(pessoaRepository.existsByCpf(pacienteSimulado.getPessoa().getCpf())).thenReturn(true);

        BancoDeDadosException exception = assertThrows(
                BancoDeDadosException.class,
                () -> pacienteService.save(pacienteInputSimulado)
        );

        assertNotNull(exception);
        assertEquals(mensagemEsperada, exception.getMessage());
    }

    @Test
    void testUpdate() throws EntityNotFound {
        PacienteInputDTO pacienteInputModificado = getPacienteInputSimulado();
        pacienteInputModificado.setNome("Isabela da Rosa");
        PacienteEntity pacienteModificado = getPacienteSimulado();
        PessoaEntity pessoaModificada = pacienteModificado.getPessoa();
        pessoaModificada.setNome("Isabela da Rosa");
        pacienteOutputSimulado.setNome("Isabela da Rosa");

        when(objectMapper.convertValue(pacienteInputModificado, PessoaEntity.class)).thenReturn(pessoaModificada);
        when(objectMapper.convertValue(pacienteInputModificado, PacienteEntity.class)).thenReturn(pacienteModificado);
        when(pacienteRepository.findById(pacienteSimulado.getIdPaciente())).thenReturn(Optional.of(pacienteSimulado));
        when(pacienteRepository.save(pacienteSimulado)).thenReturn(pacienteSimulado);
        when(objectMapper.convertValue(pacienteSimulado, PacienteOutputDTO.class)).thenReturn(pacienteOutputSimulado);

        PacienteOutputDTO resultado = pacienteService.update(pacienteSimulado.getIdPaciente(), pacienteInputModificado);

        verify(objectMapper).convertValue(pacienteInputModificado, PessoaEntity.class);
        verify(objectMapper).convertValue(pacienteInputModificado, PacienteEntity.class);
        verify(pacienteRepository).findById(pacienteSimulado.getIdPaciente());
        verify(pessoaRepository).save(pacienteSimulado.getPessoa());
        verify(pacienteRepository).save(pacienteSimulado);
        verify(objectMapper).convertValue(pacienteSimulado, PacienteOutputDTO.class);

        assertDoesNotThrow(() -> new EntityNotFound("Paciente não encontrado"));
        assertNotNull(resultado);
        assertEquals(pacienteOutputSimulado, resultado);
    }

    @Test
    void testDelete() throws EntityNotFound {
        UsuarioEntity usuarioSimulado = getUsuarioSimulado();

        when(pacienteRepository.findById(pacienteSimulado.getIdPaciente())).thenReturn(Optional.of(pacienteSimulado));
        when(usuarioService.findByLogin(pacienteSimulado.getPessoa().getCpf())).thenReturn(Optional.of(usuarioSimulado));

        pacienteService.delete(pacienteSimulado.getIdPaciente());

        verify(pacienteRepository).findById(pacienteSimulado.getIdPaciente());
        verify(usuarioService).findByLogin(pacienteSimulado.getPessoa().getCpf());
        verify(usuarioService).remove(usuarioSimulado.getIdUsuario());
        verify(pacienteRepository).delete(pacienteSimulado);

        assertDoesNotThrow(() -> new EntityNotFound("Paciente não encontrado"));
        assertDoesNotThrow(() -> new EntityNotFound("Usuário não encontrado"));
    }

    private static AtendimentoEntity getAtendimentoDoPacienteSimulado(PacienteEntity paciente){
        MedicoEntity medico = new MedicoEntity();
        medico.setIdMedico(1);

        AtendimentoEntity atendimento = new AtendimentoEntity(
                1, paciente.getHospitalEntity(), paciente, medico, LocalDate.parse("2023-08-28"),
                "Dor de cabeça", TipoDeAtendimento.CONSULTA, "Dipirona", 200.0
        );

        paciente.getAtendimentos().add(atendimento);

        return atendimento;
    }

    private static PacienteEntity getPacienteSimulado(){
        PacienteEntity paciente = new PacienteEntity();

        HospitalEntity hospital = new HospitalEntity();
        hospital.setIdHospital(1);

        PessoaEntity pessoa = new PessoaEntity(
                1, "Isabela", "60510370", LocalDate.parse("1983-05-07"),
                "55393242930", 800.0, "isabela-darosa72@gmail.com"
        );

        paciente.setIdPaciente(1);
        paciente.setHospitalEntity(hospital);
        paciente.setPessoa(pessoa);
        paciente.setAtendimentos(new HashSet<>());

        return paciente;
    }

    private static PacienteInputDTO getPacienteInputSimulado(){
        PacienteInputDTO pacienteInput = new PacienteInputDTO();

        pacienteInput.setNome("Isabela");
        pacienteInput.setCep("60510370");
        pacienteInput.setDataNascimento(LocalDate.parse("1983-05-07"));
        pacienteInput.setCpf("55393242930");
        pacienteInput.setEmail("isabela-darosa72@gmail.com");
        pacienteInput.setIdHospital(1);

        return pacienteInput;
    }

    private static PacienteOutputDTO getPacienteOutputSimulado(){
        PacienteOutputDTO pacienteOutput = new PacienteOutputDTO();

        pacienteOutput.setIdPaciente(1);
        pacienteOutput.setIdHospital(1);
        pacienteOutput.setIdPessoa(1);
        pacienteOutput.setNome("Isabela");
        pacienteOutput.setCep("60510370");
        pacienteOutput.setDataNascimento(LocalDate.parse("1983-05-07"));
        pacienteOutput.setCpf("55393242930");
        pacienteOutput.setEmail("isabela-darosa72@gmail.com");

        return pacienteOutput;
    }

    private static PacienteAtendimentosOutputDTO getPacienteAtendimentosOutputSimulado(){
        PacienteAtendimentosOutputDTO pacienteAtendimentosOutput = new PacienteAtendimentosOutputDTO();

        pacienteAtendimentosOutput.setIdPaciente(1);
        pacienteAtendimentosOutput.setNome("Isabela");

        AtendimentoOutputDTO atendimentoOutput = new AtendimentoOutputDTO(
                1, 1, 1, 1, LocalDate.parse("2023-08-28"),
                "Dor de cabeça", TipoDeAtendimento.CONSULTA.name(), "Dipirona", 200.0
        );
        pacienteAtendimentosOutput.setAtendimentos(List.of(atendimentoOutput));

        return pacienteAtendimentosOutput;
    }

    private static PacienteNovoOutputDTO getPacienteNovoOutputSimulado(){
        PacienteNovoOutputDTO pacienteNovoOutput = new PacienteNovoOutputDTO();
        pacienteNovoOutput.setIdPaciente(1);
        return pacienteNovoOutput;
    }

    private static UsuarioEntity getUsuarioSimulado(){
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(1);
        usuario.setLogin("55393242930");
        usuario.setSenha("1234");
        usuario.setCargos(new HashSet<>());
        return usuario;
    }

    private static UsuarioInputDTO getUsuarioInputSimulado(){
        UsuarioInputDTO usuarioInput = new UsuarioInputDTO();

        usuarioInput.setLogin("55393242930");
        usuarioInput.setSenha("1234");
        usuarioInput.setCargos(new HashSet<>());
        usuarioInput.getCargos().add(3);

        return usuarioInput;
    }

    private static UsuarioOutputDTO getUsuarioOutputSimulado(){
        UsuarioOutputDTO usuarioOutput = new UsuarioOutputDTO();

        usuarioOutput.setIdUsuario("1");
        usuarioOutput.setLogin("55393242930");
        usuarioOutput.setCargos(new HashSet<>());
        usuarioOutput.getCargos().add(3);

        return usuarioOutput;
    }

    private static Page<PacienteEntity> getPacientesPaginados(PacienteEntity paciente,
                                                              Pageable paginacao){
        List<PacienteEntity> pacientes = List.of(paciente);
        return new PageImpl<>(pacientes, paginacao, pacientes.size());
    }

    private static Page<PacienteOutputDTO> getPacientesPaginados(PacienteOutputDTO paciente,
                                                                 Pageable paginacao){
        List<PacienteOutputDTO> pacientesOutput = List.of(paciente);
        return new PageImpl<>(pacientesOutput, paginacao, pacientesOutput.size());
    }

    private static Page<PacienteAtendimentosOutputDTO> getPacientesPaginados(PacienteAtendimentosOutputDTO paciente,
                                                                             Pageable paginacao){
        List<PacienteAtendimentosOutputDTO> pacientesAtendimentos = List.of(paciente);
        return new PageImpl<>(pacientesAtendimentos, paginacao, pacientesAtendimentos.size());
    }

}