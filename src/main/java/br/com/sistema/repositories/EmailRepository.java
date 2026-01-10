package br.com.sistema.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.sistema.models.EmailModel;

public interface EmailRepository extends JpaRepository<EmailModel, UUID> {

}
