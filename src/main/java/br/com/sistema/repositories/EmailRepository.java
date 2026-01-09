package br.com.sistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sistema.models.EmailModel;

public interface EmailRepository extends JpaRepository<EmailModel, Long> {

}
