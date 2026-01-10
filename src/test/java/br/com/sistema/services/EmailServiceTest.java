package br.com.sistema.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import br.com.sistema.enums.StatusEmail;
import br.com.sistema.models.EmailModel;
import br.com.sistema.repositories.EmailRepository;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

	@Mock	// Simula o comportamento do repositório de emails
    private EmailRepository emailRepository;
    
    @Mock	// Simula o comportamento do JavaMailSender
    private JavaMailSender emailSender;
    
    @InjectMocks	// Injeta os mocks na classe EmailService
    private EmailService emailService;
    
    // Objeto de modelo de email para uso nos testes
    private EmailModel emailModel;
	
    
    // Configura um modelo de email antes de cada teste
    @BeforeEach
    void setUp() {
        emailModel = new EmailModel();
        emailModel.setEmailFrom("remetente@test.com.br");
        emailModel.setEmailTo("destinatario@test.com.br");
        emailModel.setSubject("Assunto Teste");
        emailModel.setText("Conteúdo do email de teste");
    }
    
    
	@Test
	void testSendEmailSuccess() {
		// Arrange (Preparar)
	    when(emailRepository.save(any(EmailModel.class))).thenReturn(emailModel);
	    
	    // Act (Executar)
	    EmailModel result = emailService.sendEmail(emailModel);
	    
	    // Assert (Verificar)
	    assertNotNull(result);
	    assertEquals(StatusEmail.SENT, result.getStatusEmail());
	    assertNotNull(result.getSendDateEmail());
	    
	    // Verifica se o emailSender.send foi chamado 1 vez
	    verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
	    
	    // Verifica se o repository.save foi chamado 1 vez
	    verify(emailRepository, times(1)).save(emailModel);
	}
	
	
	@Test
	void testSendEmailError() {
	    // Arrange (Preparar) - Simula erro no envio
	    doThrow(new MailException("Erro ao enviar email") {}).when(emailSender).send(any(SimpleMailMessage.class));
	    when(emailRepository.save(any(EmailModel.class))).thenReturn(emailModel);
	    
	    // Act (Executar)
	    EmailModel result = emailService.sendEmail(emailModel);
	    
	    // Assert (Verificar)
	    assertNotNull(result);
	    assertEquals(StatusEmail.ERROR, result.getStatusEmail());
	    assertNotNull(result.getSendDateEmail());
	    
	    // Verifica se tentou enviar o email
	    verify(emailSender, times(1)).send(any(SimpleMailMessage.class));
	    
	    // Verifica se salvou mesmo com erro
	    verify(emailRepository, times(1)).save(emailModel);
	}

	
	@Test
	void testFindAll() {
		// Arrange (Preparar)
	    Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
	    
	    List<EmailModel> emailList = Arrays.asList(emailModel);
	    Page<EmailModel> emailPage = new PageImpl<>(emailList, pageable, emailList.size());
	    
	    when(emailRepository.findAll(pageable)).thenReturn(emailPage);
	    
	    // Act (Executar)
	    Page<EmailModel> result = emailService.findAll(pageable);
	    
	    // Assert (Verificar)
	    assertNotNull(result);
	    assertEquals(1, result.getTotalElements());
	    assertEquals(1, result.getContent().size());
	    assertEquals(emailModel, result.getContent().get(0));
	    
	    // Verifica se o repository.findAll foi chamado 1 vez
	    verify(emailRepository, times(1)).findAll(pageable);
	}

	
	@Test
	void testFindByIdFound() {
		// Arrange (Preparar)
	    UUID id = UUID.randomUUID();
	    emailModel.setId(id);
	    
	    when(emailRepository.findById(id)).thenReturn(Optional.of(emailModel));
	    
	    // Act (Executar)
	    Optional<EmailModel> result = emailService.findById(id);
	    
	    // Assert (Verificar)
	    assertTrue(result.isPresent());
	    assertEquals(emailModel, result.get());
	    assertEquals(id, result.get().getId());
	    
	    // Verifica se o repository.findById foi chamado 1 vez
	    verify(emailRepository, times(1)).findById(id);
	}
	
	
	@Test
	void testFindByIdNotFound() {
	    // Arrange (Preparar)
	    UUID id = UUID.randomUUID();
	    
	    when(emailRepository.findById(id)).thenReturn(Optional.empty());
	    
	    // Act (Executar)
	    Optional<EmailModel> result = emailService.findById(id);
	    
	    // Assert (Verificar)
	    assertFalse(result.isPresent());
	    assertTrue(result.isEmpty());
	    
	    // Verifica se o repository.findById foi chamado 1 vez
	    verify(emailRepository, times(1)).findById(id);
	}

}
