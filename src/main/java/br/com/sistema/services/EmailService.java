package br.com.sistema.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.sistema.dtos.PortfolioEmailDto;
import br.com.sistema.enums.StatusEmail;
import br.com.sistema.models.EmailModel;
import br.com.sistema.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailService {

	Logger logger = LogManager.getLogger(EmailService.class);

	@Autowired
	EmailRepository emailRepository;

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private EmailTemplateService emailTemplateService;

	@Value("${spring.mail.username}")
	private String emailFrom;

	
	
	// ===========================================================================
	// Envia um email genérico
	// ===========================================================================
	@Transactional
	public EmailModel sendEmail(EmailModel emailModel) {

		emailModel.setSendDateEmail(LocalDateTime.now());

		try {
			MimeMessage mimeMessage = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setFrom(emailModel.getEmailFrom());
			helper.setTo(emailModel.getEmailTo());
			helper.setSubject(emailModel.getSubject());

			// Se tiver HTML, envia como HTML, senão envia como texto simples
			if (emailModel.getHtml() != null && !emailModel.getHtml().isEmpty()) {
				helper.setText(emailModel.getText(), emailModel.getHtml());
			} else {
				helper.setText(emailModel.getText(), false);
			}

			emailSender.send(mimeMessage);
			emailModel.setStatusEmail(StatusEmail.SENT);
			logger.info("Email sent successfully to: {} with subject: {}", emailModel.getEmailTo(),
					emailModel.getSubject());

		} catch (MailException | MessagingException e) {
			emailModel.setStatusEmail(StatusEmail.ERROR);
			logger.error("Failed to send email to: {} | Subject: {} | Error: {}", emailModel.getEmailTo(),
					emailModel.getSubject(), e.getMessage());

		} finally {
			emailModel = emailRepository.save(emailModel);
			logger.info("Email saved with status: {} | emailId: {}", emailModel.getStatusEmail(), emailModel.getId());
		}

		return emailModel;
	}

	
	
	// ===========================================================================
	// Envia um email de contato usando template para o portfólio
	// ===========================================================================
	@Transactional
	public EmailModel sendPortfolioEmail(PortfolioEmailDto portfolioEmailDto) throws IOException {
		logger.info("Processing contact email from: {} | Subject: {}", portfolioEmailDto.email(), portfolioEmailDto.subject());

		// Prepara as variáveis do template
		Map<String, String> variables = new HashMap<>();
		variables.put("name", portfolioEmailDto.name());
		variables.put("email", portfolioEmailDto.email());
		variables.put("phone", portfolioEmailDto.phone());
		variables.put("subject", portfolioEmailDto.subject());
		variables.put("message", portfolioEmailDto.message());

		// Processa o template - escolhe o nome do template e insere as variáveis no HTML
		String htmlContent = emailTemplateService.loadAndProcessTemplate("template-email-gemini.html", variables);

		// Cria o EmailModel
		EmailModel emailModel = new EmailModel();
		emailModel.setOwnerRef(portfolioEmailDto.ownerRef());
		emailModel.setEmailFrom(emailFrom);
		emailModel.setEmailTo(portfolioEmailDto.emailTo());
		emailModel.setSubject("Novo contato: " + portfolioEmailDto.subject());
		emailModel.setText(buildPlainTextContent(portfolioEmailDto));
		emailModel.setHtml(htmlContent);

		// Envia o email usando o método já existente
		return sendEmail(emailModel);
	}

	// ===========================================================================
	// Envia um email de contato usando template para o portfólio
	// ===========================================================================
	@Transactional
	public EmailModel sendCoolifyEmail(PortfolioEmailDto portfolioEmailDto) throws IOException {
		logger.info("Processing contact email from: {} | Subject: {}", portfolioEmailDto.email(), portfolioEmailDto.subject());

		// Prepara as variáveis do template
		Map<String, String> variables = new HashMap<>();
		variables.put("name", portfolioEmailDto.name());
		variables.put("email", portfolioEmailDto.email());
		variables.put("phone", portfolioEmailDto.phone());
		variables.put("subject", portfolioEmailDto.subject());
		variables.put("message", portfolioEmailDto.message());

		// Processa o template - escolhe o nome do template e insere as variáveis no HTML
		String htmlContent = emailTemplateService.loadAndProcessTemplate("template-email-coolify.html", variables);

		// Cria o EmailModel
		EmailModel emailModel = new EmailModel();
		emailModel.setOwnerRef(portfolioEmailDto.ownerRef());
		emailModel.setEmailFrom(emailFrom);
		emailModel.setEmailTo(portfolioEmailDto.emailTo());
		emailModel.setSubject("Novo contato: " + portfolioEmailDto.subject());
		emailModel.setText(buildPlainTextContent(portfolioEmailDto));
		emailModel.setHtml(htmlContent);

		// Envia o email usando o método já existente
		return sendEmail(emailModel);
	}

	
	
	// ===========================================================================
	// Constrói o conteúdo de texto simples do email de contato
	// ===========================================================================
	private String buildPlainTextContent(PortfolioEmailDto contactDto) {
		return String.format("Nome: %s%nEmail: %s%nTelefone: %s%nAssunto: %s%nMensagem: %s", contactDto.name(),
				contactDto.email(), contactDto.phone(), contactDto.subject(), contactDto.message());
	}

	public Page<EmailModel> findAll(Pageable pageable) {
		return emailRepository.findAll(pageable);
	}

	public Optional<EmailModel> findById(UUID id) {
		return emailRepository.findById(id);
	}
}