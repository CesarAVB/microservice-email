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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.sistema.enums.StatusEmail;
import br.com.sistema.models.EmailModel;
import br.com.sistema.repositories.EmailRepository;
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
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(emailModel.getEmailFrom());
			message.setTo(emailModel.getEmailTo());
			message.setSubject(emailModel.getSubject());
			message.setText(emailModel.getText());
			emailSender.send(message);
			emailModel.setStatusEmail(StatusEmail.SENT);
			logger.info("Email sent successfully to: {} ", emailModel.getEmailTo());
			
		} catch (MailException e) {
			emailModel.setStatusEmail(StatusEmail.ERROR);
			logger.error("Email with error: {} ", emailModel.toString());
	        logger.error("Error {} ", e);
			
		} 
		
		emailModel = emailRepository.save(emailModel);
		logger.info("Email saved successfully emailId: {} ", emailModel.getId());
		return emailModel;
	}
	
	
	 public Page<EmailModel> findAll(Pageable pageable) {
        return  emailRepository.findAll(pageable);
    }

    public Optional<EmailModel> findById(UUID id) {
        return emailRepository.findById(id);
    }
}