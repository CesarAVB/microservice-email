package br.com.sistema.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CoolifyWebhookDto(
    @NotNull(message = "O status de sucesso é obrigatório")
    Boolean success,
    
    @NotBlank(message = "A mensagem é obrigatória")
    String message,
    
    @NotBlank(message = "O evento é obrigatório")
    String event,
    
    @NotBlank(message = "O nome da aplicação é obrigatório")
    @JsonProperty("application_name")
    String applicationName,
    
    @NotBlank(message = "O UUID da aplicação é obrigatório")
    @JsonProperty("application_uuid")
    String applicationUuid,
    
    @NotBlank(message = "O UUID do deployment é obrigatório")
    @JsonProperty("deployment_uuid")
    String deploymentUuid,
    
    @NotBlank(message = "A URL do deployment é obrigatória")
    @JsonProperty("deployment_url")
    String deploymentUrl,
    
    @NotBlank(message = "O projeto é obrigatório")
    String project,
    
    @NotBlank(message = "O ambiente é obrigatório")
    String environment,
    
    @NotBlank(message = "O FQDN é obrigatório")
    String fqdn
) {
}