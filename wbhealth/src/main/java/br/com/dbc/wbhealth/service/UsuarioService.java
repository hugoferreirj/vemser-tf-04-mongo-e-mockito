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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CargoService cargoService;
    private final ObjectMapper objectMapper;
    private final LogService logService;

    public UsuarioEntity findById(Integer idUsuario) throws EntityNotFound {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFound("Usuário não encontrado"));
    }

    public Optional<UsuarioEntity> findByLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }

    public Integer getIdLoggedUser() throws RegraDeNegocioException {
        String idEmString = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer idUsuario;

        try{
            idUsuario = Integer.parseInt(idEmString);
        }catch (NumberFormatException e){
            throw new RegraDeNegocioException("Não existe nenhum usuário logado");
        }

        return idUsuario;
    }

    public UsuarioOutputDTO getLoggedUser() throws RegraDeNegocioException, EntityNotFound {
        UsuarioEntity usuario = findById(getIdLoggedUser());
        return convertUsuarioToOutput(usuario);
    }

    public String login(UsuarioLoginInputDTO usuarioLoginInput,
                        AuthenticationManager authenticationManager,
                        TokenService tokenService) throws RegraDeNegocioException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(usuarioLoginInput.getLogin(), usuarioLoginInput.getSenha());

        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new RegraDeNegocioException("Usuário ou senha inválidos");
        }

        UsuarioEntity usuarioValidado = (UsuarioEntity) authentication.getPrincipal();
        String tokenGerado = tokenService.generateToken(usuarioValidado);

        logService.create(Descricao.LOGIN, usuarioValidado.getIdUsuario());
        return tokenGerado;
    }

    public UsuarioOutputDTO create(UsuarioInputDTO usuarioInput) throws EntityNotFound, RegraDeNegocioException {
        if (usuarioRepository.existsByLogin(usuarioInput.getLogin())) {
            throw new RegraDeNegocioException("Nome de usuário já está em uso.");
        }

        String senhaCriptografada = passwordEncoder.encode(usuarioInput.getSenha());

        UsuarioEntity usuarioEntity = convertInputToUsuario(usuarioInput);
        usuarioEntity.setSenha(senhaCriptografada);

        logService.create(Descricao.CREATE, getIdLoggedUser());
        return convertUsuarioToOutput(usuarioRepository.save(usuarioEntity));
    }

    public UsuarioOutputDTO update(Integer idUsuario, UsuarioInputDTO usuarioInput) throws EntityNotFound {
        try {
            UsuarioEntity usuarioDesatualizado = findById(idUsuario);
            if (usuarioRepository.existsByLogin(usuarioInput.getLogin())) {
                if (!usuarioDesatualizado.getLogin().equals(usuarioInput.getLogin())) {
                    throw new RegraDeNegocioException("Nome de usuário é utilizado por outro usuário.");
                }
            }
            UsuarioEntity entity = convertInputToUsuario(usuarioInput);
            String senhaCriptografada = passwordEncoder.encode(usuarioInput.getSenha());
            entity.setSenha(senhaCriptografada);

            BeanUtils.copyProperties(entity, usuarioDesatualizado, "idUsuario");

            UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioDesatualizado);

            logService.create(Descricao.UPDATE, getIdLoggedUser());
            return convertUsuarioToOutput(usuarioAtualizado);
        } catch (RegraDeNegocioException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(UsuarioSenhaInputDTO usuarioSenhaInput) throws EntityNotFound, RegraDeNegocioException {
        UsuarioEntity usuarioParaEditar = findById(getIdLoggedUser());
        String senhaCriptografada = passwordEncoder.encode(usuarioSenhaInput.getSenha());
        usuarioParaEditar.setSenha(senhaCriptografada);
        usuarioRepository.save(usuarioParaEditar);
        logService.create(Descricao.UPDATE, getIdLoggedUser());
    }

    public void remove(Integer idUsuario) throws EntityNotFound {
        UsuarioEntity usuario = findById(idUsuario);
        usuarioRepository.delete(usuario);
        logService.create(Descricao.DELETE, getIdLoggedUser());
    }

    public UsuarioEntity convertInputToUsuario(UsuarioInputDTO usuarioInputDTO) throws EntityNotFound {
        UsuarioEntity entity = objectMapper.convertValue(usuarioInputDTO, UsuarioEntity.class);
        Set<CargoEntity> cargos = new HashSet<>();
        if (usuarioInputDTO.getCargos() != null) {
            for (Integer idCargo : usuarioInputDTO.getCargos()) {
                CargoEntity cargo = cargoService.findById(idCargo);
                cargos.add(cargo);
            }
            entity.setCargos(cargos);
        }
        entity.setCargos(cargos);
        return entity;
    }

    public UsuarioOutputDTO convertUsuarioToOutput(UsuarioEntity entity) {
        UsuarioOutputDTO usuarioOutputDTO = objectMapper.convertValue(entity, UsuarioOutputDTO.class);
        Set<Integer> cargos = new HashSet<>();
        for (CargoEntity cargo : entity.getCargos()) {
            cargos.add(cargo.getIdCargo());
        }
        usuarioOutputDTO.setCargos(cargos);
        return usuarioOutputDTO;
    }

    public String generateRandomPassword(){
        Random random = new Random();
        Integer randomNumber = random.nextInt(1000, 9999);
        return String.valueOf(randomNumber);
    }

    protected UsuarioInputDTO criarUsuarioInput(String login, Integer cargo){
        UsuarioInputDTO usuarioInput = new UsuarioInputDTO();

        usuarioInput.setLogin(login);
        usuarioInput.setSenha(generateRandomPassword());
        usuarioInput.setCargos(new HashSet<>());
        usuarioInput.getCargos().add(cargo);

        return usuarioInput;
    }

}

