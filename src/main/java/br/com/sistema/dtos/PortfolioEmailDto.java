package br.com.sistema.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PortfolioEmailDto(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,
    
    @NotBlank(message = "O telefone é obrigatório")
    String phone,
    
    @NotBlank(message = "O assunto é obrigatório")
    String subject,
    
    @NotBlank(message = "A mensagem é obrigatória")
    String message,
    
    @NotBlank(message = "A referência do proprietário é obrigatória")
    String ownerRef,
    
    @NotBlank(message = "O email de destino é obrigatório")
    @Email(message = "Email de destino inválido")
    String emailTo
) {
}