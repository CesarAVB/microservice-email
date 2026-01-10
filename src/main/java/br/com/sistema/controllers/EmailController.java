package br.com.sistema.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.sistema.dtos.EmailDto;
import br.com.sistema.models.EmailModel;
import br.com.sistema.services.EmailService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api") // Prefixo comum para todos os endpoints
@Tag(name = "Emails", description = "Endpoints para envio e consulta de emails")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping("/sending-email")
    @Operation(summary = "Enviar email", description = "Envia um email e registra no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Email enviado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao enviar o email")
    })
    public ResponseEntity<EmailModel> sendingEmail(@RequestBody @Valid EmailDto emailDto) {
        EmailModel emailModel = new EmailModel();
        BeanUtils.copyProperties(emailDto, emailModel);
        emailService.sendEmail(emailModel);
        return new ResponseEntity<>(emailModel, HttpStatus.CREATED);
    }

    @GetMapping("/emails")
    @Operation(summary = "Listar emails", description = "Retorna uma lista paginada de emails enviados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de emails retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao buscar emails")
    })
    public ResponseEntity<Page<EmailModel>> getAllEmails(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(emailService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/emails/{id}")
    @Operation(summary = "Consultar email por ID", description = "Retorna os detalhes de um email específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email encontrado"),
        @ApiResponse(responseCode = "404", description = "Email não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno ao buscar email")
    })
    public ResponseEntity<Object> getOneEmail(@PathVariable(value = "id") UUID id) {
        Optional<EmailModel> emailModelOptional = emailService.findById(id);
        if (!emailModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(emailModelOptional.get());
        }
    }
}
