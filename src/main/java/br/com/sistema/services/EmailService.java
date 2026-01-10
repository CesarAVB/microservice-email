package br.com.sistema.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.sistema.enums.StatusEmail;
import br.com.sistema.models.EmailModel;
import br.com.sistema.repositories.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailService {
	
	// Cria um logger para a classe EmailController
	Logger logger = LogManager.getLogger(EmailService.class);

	@Autowired
	EmailRepository emailRepository;
	
	@Autowired
	private JavaMailSender emailSender;

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
			logger.info("Email sent successfully to: {} with subject: {}", emailModel.getEmailTo(), emailModel.getSubject());
			
		} catch (MailException | MessagingException e) {
			emailModel.setStatusEmail(StatusEmail.ERROR);
			logger.error("Failed to send email to: {} | Subject: {} | Error: {}", emailModel.getEmailTo(), emailModel.getSubject(), e.getMessage());
			
		} finally {
	        emailModel = emailRepository.save(emailModel);
	        logger.info("Email saved with status: {} | emailId: {}", emailModel.getStatusEmail(), emailModel.getId());
	    }

		return emailModel;
	}
	
	
	 public Page<EmailModel> findAll(Pageable pageable) {
        return  emailRepository.findAll(pageable);
    }

    public Optional<EmailModel> findById(UUID id) {
        return emailRepository.findById(id);
    }
}