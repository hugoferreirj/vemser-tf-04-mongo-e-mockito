package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.exceptions.BancoDeDadosException;
import br.com.dbc.wbhealth.exceptions.EntityNotFound;
import br.com.dbc.wbhealth.model.dto.hospital.HospitalOutputDTO;
import br.com.dbc.wbhealth.model.dto.medico.MedicoInputDTO;
import br.com.dbc.wbhealth.model.dto.medico.MedicoNovoOutputDTO;
import br.com.dbc.wbhealth.model.dto.medico.MedicoOutputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioInputDTO;
import br.com.dbc.wbhealth.model.dto.usuario.UsuarioOutputDTO;
import br.com.dbc.wbhealth.model.entity.*;
import br.com.dbc.wbhealth.repository.MedicoRepository;
import br.com.dbc.wbhealth.repository.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MedicoServiceTest {

    @InjectMocks
    MedicoService medicoService;

    @Mock
    PessoaRepository pessoaRepository;
    @Mock
    MedicoRepository medicoRepository;
    @Mock
    UsuarioService usuarioService;
    @Mock
    HospitalService hospitalService;

    @Mock
    ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

    }

    @Test
    public void testSave() throws BancoDeDadosException, EntityNotFound, MessagingException {
        // Criar as entradas simuladas
        MedicoInputDTO medicoInput = createMedicoInputDTO();

        // Simular comportamento dos métodos nos mocks
        PessoaEntity pessoaEntity = createPessoaEntity();

        when(objectMapper.convertValue(medicoInput, PessoaEntity.class)).thenReturn(pessoaEntity);

        when(pessoaRepository.existsByCpf(any())).thenReturn(false);
        when(medicoRepository.existsByCrm(any())).thenReturn(false);

        UsuarioInputDTO usuarioInput = createUsuarioInputDTO();

        when(usuarioService.criarUsuarioInput(medicoInput.getCpf(), 4)).thenReturn(usuarioInput);

        UsuarioOutputDTO usuarioMock = createUsuarioOutputDTO();

        when(usuarioService.create(usuarioInput)).thenReturn(usuarioMock);

        when(pessoaRepository.save(any())).thenReturn(pessoaEntity);

        MedicoEntity medicoEntity = createMedicoEntity();

        when(medicoRepository.save(any())).thenReturn(medicoEntity);
        medicoEntity.setIdMedico(1);
        MedicoNovoOutputDTO medicoNovoOutput = createMedicoNovoOutputDTO();

        when(objectMapper.convertValue(medicoEntity, MedicoNovoOutputDTO.class)).thenReturn(medicoNovoOutput);

        // Chamar o método a ser testado

        MedicoNovoOutputDTO medicoNovoOutputDTO = medicoService.save(medicoInput);

        // ACT
        Assertions.assertNotNull(medicoNovoOutputDTO);
        Assertions.assertEquals(medicoNovoOutput, medicoNovoOutputDTO);
    }


    @Test
    public void findByIdTest() throws EntityNotFound {
        MedicoOutputDTO medicoOutputEsperado = createMedicoOutputDTO();

        Integer idMedico = 1;
        MedicoEntity medicoEntity = createMedicoEntity();
        medicoEntity.setIdMedico(idMedico);

        when(medicoRepository.findById(1)).thenReturn(Optional.of(medicoEntity));

        when(objectMapper.convertValue(medicoEntity, MedicoOutputDTO.class)).thenReturn(medicoOutputEsperado);

        MedicoOutputDTO medicoOutput = medicoService.findById(idMedico);

        Assertions.assertNotNull(medicoOutput);
        Assertions.assertEquals(medicoOutputEsperado, medicoOutput);
    }

    @Test
    public void findAllTest() {
        MedicoEntity medico1 = createMedicoEntity();
        MedicoEntity medico2 = createMedicoEntity();
        MedicoOutputDTO medicoOutput1 = createMedicoOutputDTO();
        MedicoOutputDTO medicoOutput2 = createMedicoOutputDTO();

        List<MedicoEntity> MedicoList = new ArrayList<>();
        MedicoList.add(medico1);
        MedicoList.add(medico2);
        Page<MedicoEntity> MedicoPage = new PageImpl<>(MedicoList);

        List<MedicoOutputDTO> medicoDTOList = new ArrayList<>();
        medicoDTOList.add(medicoOutput1);
        medicoDTOList.add(medicoOutput2);
        Page<MedicoOutputDTO> MeidcoDTOPage = new PageImpl<>(medicoDTOList);

        when(medicoRepository.findAll(any(Pageable.class))).thenReturn(MedicoPage);

        when(objectMapper.convertValue(medico1, MedicoOutputDTO.class)).thenReturn(medicoOutput1);
        when(objectMapper.convertValue(medico2, MedicoOutputDTO.class)).thenReturn(medicoOutput2);

        Page<MedicoOutputDTO> result = medicoService.findAll(0, 5);

        verify(medicoRepository, times(1)).findAll(any(Pageable.class));
        Assertions.assertEquals(MeidcoDTOPage.getTotalElements(), result.getTotalElements());
        Assertions.assertIterableEquals(MeidcoDTOPage, result);

    }

    @Test
    public void deleteTest() throws EntityNotFound {
        Integer idMedico = 1;
        MedicoEntity medicoEntity = createMedicoEntity();
        medicoEntity.setIdMedico(idMedico);

        UsuarioEntity usuarioEntity = createUsuarioEntity();

        when(usuarioService.findByLogin(medicoEntity.getPessoa().getCpf())).thenReturn(Optional.of(usuarioEntity));

        when(medicoRepository.findById(1)).thenReturn(Optional.of(medicoEntity));

        // Act
        medicoService.delete(idMedico);

        // Assert
        verify(usuarioService).remove(usuarioEntity.getIdUsuario());
        verify(medicoRepository, times(1)).findById(idMedico);
    }

    @Test
    public void updateTest() throws EntityNotFound {
        MedicoInputDTO medicoInput = createMedicoInputDTO();

        PessoaEntity pessoaEntity = createPessoaEntity();
//        PessoaEntity pessoaModificada = createPessoaEntity();
        pessoaEntity.setIdPessoa(1);

        when(objectMapper.convertValue(medicoInput, PessoaEntity.class)).thenReturn(pessoaEntity);

        MedicoEntity medicoEntity = createMedicoEntity();
        medicoEntity.setIdMedico(1);

        when(medicoRepository.findById(1)).thenReturn(Optional.of(medicoEntity));
        when(pessoaRepository.save(any())).thenReturn(pessoaEntity);

        when(medicoRepository.save(any())).thenReturn(medicoEntity);

        MedicoOutputDTO medicoOutput = createMedicoOutputDTO();

        when(objectMapper.convertValue(medicoEntity, MedicoOutputDTO.class)).thenReturn(medicoOutput);

        MedicoOutputDTO medicoUpdated = medicoService.update(1, medicoInput);

        Assertions.assertNotNull(medicoUpdated);
        Assertions.assertEquals(medicoOutput, medicoUpdated);
    }

    private static MedicoEntity createMedicoEntity() {
        MedicoEntity medicoMock = new MedicoEntity();
        medicoMock.setCrm("AM-7654321/82");
        medicoMock.setPessoa(createPessoaEntity());
        medicoMock.setHospitalEntity(createHospitalEntity());

        return medicoMock;
    }

    private static MedicoInputDTO createMedicoInputDTO() {
        MedicoInputDTO medicoInput = new MedicoInputDTO();
        medicoInput.setNome("Maria");
        medicoInput.setCep("12345123");
        medicoInput.setDataNascimento(LocalDate.of(1995, 07, 15));
        medicoInput.setCpf("05281583093");
        medicoInput.setSalarioMensal(5000.0);
        medicoInput.setEmail("maria@email.com");
        medicoInput.setIdHospital(1);
        medicoInput.setCrm("AM-7654321/82");

        return medicoInput;
    }

    private static MedicoOutputDTO createMedicoOutputDTO() {
        MedicoOutputDTO medicoOutput = new MedicoOutputDTO();
        medicoOutput.setIdMedico(1);
        medicoOutput.setNome("Maria");
        medicoOutput.setCep("12345123");
        medicoOutput.setDataNascimento(LocalDate.of(1995, 07, 15));
        medicoOutput.setCpf("05281583093");
        medicoOutput.setSalarioMensal(5000.0);
        medicoOutput.setEmail("maria@email.com");
        medicoOutput.setIdHospital(1);
        medicoOutput.setCrm("AM-7654321/82");
        medicoOutput.setIdPessoa(1);

        return medicoOutput;
    }

    private static MedicoNovoOutputDTO createMedicoNovoOutputDTO() {
        MedicoNovoOutputDTO medicoNovoOutput = new MedicoNovoOutputDTO();
        medicoNovoOutput.setCrm("AM-7654321/82");
        medicoNovoOutput.setIdHospital(1);
        medicoNovoOutput.setNome("Maria");
        medicoNovoOutput.setCep("12345123");
        medicoNovoOutput.setDataNascimento(LocalDate.of(1995, 07, 15));
        medicoNovoOutput.setCpf("05281583093");
        medicoNovoOutput.setSalarioMensal(5000.0);
        medicoNovoOutput.setEmail("maria@email.com");
        medicoNovoOutput.setIdPessoa(1);
        medicoNovoOutput.setUsuario(createUsuarioOutputDTO());

        return medicoNovoOutput;
    }


    private static PessoaEntity createPessoaEntity() {
        PessoaEntity pessoaMock = new PessoaEntity();
        pessoaMock.setIdPessoa(1);
        pessoaMock.setNome("Maria");
        pessoaMock.setCep("12345123");
        pessoaMock.setDataNascimento(LocalDate.of(1995, 07, 15));
        pessoaMock.setCpf("05281583093");
        pessoaMock.setSalarioMensal(5000.0);
        pessoaMock.setEmail("AM-7654321/82");

        return pessoaMock;
    }

    private static UsuarioEntity createUsuarioEntity() {
        CargoEntity cargo = new CargoEntity();
        cargo.setIdCargo(4);
        cargo.setNome("MEDICO");
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.getCargos().add(cargo);
        usuarioEntity.setLogin("05281583093");

        return usuarioEntity;
    }

    private static UsuarioInputDTO createUsuarioInputDTO() {
        Set<Integer> cargos = new HashSet<>();
        cargos.add(4);
        UsuarioInputDTO usuarioInput = new UsuarioInputDTO();
        usuarioInput.setCargos(cargos);
        usuarioInput.setLogin("05281583093");

        return usuarioInput;
    }

    private static UsuarioOutputDTO createUsuarioOutputDTO() {
        Set<Integer> cargos = new HashSet<>();
        cargos.add(4);
        UsuarioOutputDTO usuarioMock = new UsuarioOutputDTO();
        usuarioMock.setIdUsuario("1");
        usuarioMock.setCargos(cargos);
        usuarioMock.setLogin("05281583093");

        return usuarioMock;
    }

    private static HospitalEntity createHospitalEntity() {
        HospitalEntity hospitalMock = new HospitalEntity();
        hospitalMock.setIdHospital(1);
        hospitalMock.setNome("Hospital SMA");

        return hospitalMock;
    }

    private static HospitalOutputDTO createHospitalOutputDTO() {
        HospitalOutputDTO hospitalOutput = new HospitalOutputDTO();
        hospitalOutput.setIdHospital(1);
        hospitalOutput.setNome("Hospital SMA");

        return hospitalOutput;
    }

}


//import br.com.dbc.wbhealth.exceptions.BancoDeDadosException;
//import br.com.dbc.wbhealth.exceptions.EntityNotFound;
//import br.com.dbc.wbhealth.model.dto.hospital.HospitalOutputDTO;
//import br.com.dbc.wbhealth.model.dto.medico.MedicoInputDTO;
//import br.com.dbc.wbhealth.model.dto.medico.MedicoNovoOutputDTO;
//import br.com.dbc.wbhealth.model.dto.usuario.UsuarioOutputDTO;
//import br.com.dbc.wbhealth.model.entity.HospitalEntity;
//import br.com.dbc.wbhealth.model.entity.MedicoEntity;
//import br.com.dbc.wbhealth.model.entity.PessoaEntity;
//import br.com.dbc.wbhealth.repository.MedicoRepository;
//import br.com.dbc.wbhealth.repository.PessoaRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import javax.mail.MessagingException;
//import java.time.LocalDate;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class MedicoServiceTest {
//
//    @InjectMocks
//    MedicoService medicoService;
//    @Mock
//    ObjectMapper objectMapper;
//    @Mock
//    MedicoRepository medicoRepository;
//    @Mock
//    PessoaRepository pessoaRepository;
//    @Mock
//    HospitalService hospitalService;
//
//
//
//    @Test
//    public void criarMedicoNoBanco() throws EntityNotFound, BancoDeDadosException, MessagingException {
//
//        MedicoInputDTO medicoInput = new MedicoInputDTO();
//        medicoInput.setNome("Maria");
//        medicoInput.setCep("12345123");
//        medicoInput.setDataNascimento(LocalDate.of(1995, 07, 15));
//        medicoInput.setCpf("05281583093");
//        medicoInput.setSalarioMensal(5000.0);
//        medicoInput.setEmail("maria@email.com");
//        medicoInput.setIdHospital(1);
//        medicoInput.setCrm("AM-7654321/82");
//
//        PessoaEntity pessoaMock = new PessoaEntity();
//        pessoaMock.setIdPessoa(1);
//        pessoaMock.setNome(medicoInput.getNome());
//        pessoaMock.setCep(medicoInput.getCep());
//        pessoaMock.setDataNascimento(medicoInput.getDataNascimento());
//        pessoaMock.setCpf(medicoInput.getCpf());
//        pessoaMock.setSalarioMensal(medicoInput.getSalarioMensal());
//        pessoaMock.setEmail(medicoInput.getEmail());
//
//        HospitalEntity hospitalMock = new HospitalEntity();
//        hospitalMock.setIdHospital(1);
//        hospitalMock.setNome("Hospital SMA");
//
//        MedicoEntity medicoMock = new MedicoEntity();
//        medicoMock.setIdMedico(1);
//        medicoMock.setCrm(medicoInput.getCrm());
//        medicoMock.setPessoa(pessoaMock);
//        medicoMock.setHospitalEntity(hospitalMock);
//
//        Set<Integer> cargos = new HashSet<>();
//        cargos.add(4);
//
//        UsuarioOutputDTO usuarioMock = new UsuarioOutputDTO();
//        usuarioMock.setIdUsuario("1");
//        usuarioMock.setCargos(cargos);
//        usuarioMock.setLogin(medicoInput.getCpf());
//
//        MedicoNovoOutputDTO medicoOutput = new MedicoNovoOutputDTO();
//        medicoOutput.setIdMedico(medicoMock.getIdMedico());
//        medicoOutput.setIdHospital(hospitalMock.getIdHospital());
//        medicoOutput.setIdPessoa(pessoaMock.getIdPessoa());
//        medicoOutput.setNome(medicoInput.getNome());
//        medicoOutput.setCep(medicoInput.getCep());
//        medicoOutput.setDataNascimento(medicoInput.getDataNascimento());
//        medicoOutput.setCpf(medicoInput.getCpf());
//        medicoOutput.setSalarioMensal(medicoInput.getSalarioMensal());
//        medicoOutput.setEmail(medicoInput.getEmail());
//        medicoOutput.setCrm(medicoInput.getCrm());
//        medicoOutput.setUsuario(usuarioMock);
//
//        MedicoEntity medicoTeste = medicoService.convertInputToMedico(pessoaMock, medicoInput);
//
//        // ACT
////        when(hospitalService.save(any())).thenReturn(objectMapper.convertValue(hospitalMock, HospitalOutputDTO.class));
////        when(objectMapper.convertValue(medicoInput, PessoaEntity.class)).thenReturn(pessoaMock);
//        when(pessoaRepository.save(any())).thenReturn(pessoaMock);
//        when(medicoRepository.save(any())).thenReturn(medicoMock);
//        when(medicoService.convertToMedicoNovoOutput(medicoMock, usuarioMock)).thenReturn(medicoOutput);
//
//        MedicoNovoOutputDTO medicoNovoOutput = medicoService.save(medicoTeste);
//
//        // ASSERT
//        Assertions.assertNull(medicoNovoOutput);
//        Assertions.assertEquals(medicoInput.getNome(), medicoNovoOutput.getNome());
//        Assertions.assertEquals(medicoInput.getCep(), medicoNovoOutput.getCep());
//        Assertions.assertEquals(medicoInput.getCpf(), medicoNovoOutput.getCpf());
//        Assertions.assertEquals(medicoInput.getCrm(), medicoNovoOutput.getCrm());
//        Assertions.assertEquals(medicoInput.getEmail(), medicoNovoOutput.getEmail());
//        Assertions.assertEquals(1, medicoNovoOutput.getIdMedico());
//        Assertions.assertEquals(1, medicoNovoOutput.getIdHospital());
//        Assertions.assertEquals(1, medicoNovoOutput.getUsuario().getIdUsuario());
//
//    }
//
////    void converterMedicoOutput(MedicoOutputDTO medicoOutput, PessoaEntity pessoa) {
////        medicoOutput.setNome(pessoa.getNome());
////        medicoOutput.setCep(pessoa.getCep());
////        medicoOutput.setDataNascimento(pessoa.getDataNascimento());
////        medicoOutput.setCpf(pessoa.getCpf());
////        medicoOutput.setSalarioMensal(pessoa.getSalarioMensal());
////        medicoOutput.setEmail(pessoa.getEmail());
////        medicoOutput.setIdPessoa(pessoa.getIdPessoa());
////
////    }
////
////    MedicoNovoOutputDTO convertToMedicoNovoOutput(MedicoEntity medico, UsuarioOutputDTO usuario) {
////        MedicoNovoOutputDTO medicoNovoOutput = objectMapper.convertValue(medico, MedicoNovoOutputDTO.class);
////
////        medicoNovoOutput.setIdHospital(1);
////        converterMedicoOutput(medicoNovoOutput, medico.getPessoa());
////        medicoNovoOutput.setUsuario(usuario);
////
////        return medicoNovoOutput;
////    }
//}