package br.com.dbc.wbhealth.service;

import br.com.dbc.wbhealth.model.dto.usuario.UsuarioInputDTO;
import br.com.dbc.wbhealth.model.entity.PessoaEntity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final Configuration fmConfiguration;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.username}")
    private String emailSuporte;

    public void enviarEmailUsuarioCriado(PessoaEntity pessoa, UsuarioInputDTO usuario, String cargo) throws MessagingException {
        String assunto = "Cadastro realizado - WB Health";
        String nomeTemplate = "email-template-usuario-criado.ftl";
        String endpointUpdatePassword = "http://vemser-hml.dbccompany.com.br:39000/liviafausto/vemser-tf-05-springsecurity/swagger-ui/index.html#/auth-controller/updatePassword";

        Map<String, String> dados = new HashMap<>();
        dados.put("nome", pessoa.getNome());
        dados.put("cargo", cargo);
        dados.put("login", usuario.getLogin());
        dados.put("senha", usuario.getSenha());
        dados.put("emailSuporte", emailSuporte);
        dados.put("linkParaAlterarSenha", endpointUpdatePassword);

        sendEmailTemplate(pessoa.getEmail(), assunto, nomeTemplate, dados);
    }

    private void sendEmailTemplate(String toEmail, String subject, String templateName, Map<String, String> dados)
            throws MessagingException {
        MimeMessage emailTemplate = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(emailTemplate, true);

        try{
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(getContentFromTemplate(templateName, dados), true);

            mailSender.send(helper.getMimeMessage());
        }
        catch (IOException | TemplateException e){
            e.printStackTrace();
        }
    }

    private String getContentFromTemplate(String templateName, Map<String, String> dados) throws IOException,
            TemplateException {
        Template template = fmConfiguration.getTemplate(templateName);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
    }

}
