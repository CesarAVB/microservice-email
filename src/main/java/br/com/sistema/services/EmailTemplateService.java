package br.com.sistema.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
public class EmailTemplateService {

    Logger logger = LogManager.getLogger(EmailTemplateService.class);

    /**
     * Carrega um template HTML do diretório resources/templates
     * 
     * @param templateName nome do arquivo de template (ex: "contact-email.html")
     * @return conteúdo do template como String
     * @throws IOException se o template não for encontrado
     */
    public String loadTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        
        if (!resource.exists()) {
            logger.error("Template não encontrado: {}", templateName);
            throw new IOException("Template não encontrado: " + templateName);
        }
        
        String template = StreamUtils.copyToString(
            resource.getInputStream(), 
            StandardCharsets.UTF_8
        );
        
        logger.info("Template carregado com sucesso: {}", templateName);
        return template;
    }

    /**
     * Processa um template substituindo os placeholders pelos valores fornecidos
     * 
     * @param template conteúdo do template HTML
     * @param variables mapa com os valores para substituir (chave = placeholder, valor = conteúdo)
     * @return template processado com os valores substituídos
     */
    public String processTemplate(String template, Map<String, String> variables) {
        String processedTemplate = template;
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "${contact." + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            processedTemplate = processedTemplate.replace(placeholder, value);
        }
        
        logger.debug("Template processado com {} variáveis", variables.size());
        return processedTemplate;
    }

    /**
     * Carrega e processa um template em uma única operação
     * 
     * @param templateName nome do arquivo de template
     * @param variables mapa com os valores para substituir
     * @return template processado
     * @throws IOException se o template não for encontrado
     */
    public String loadAndProcessTemplate(String templateName, Map<String, String> variables) throws IOException {
        String template = loadTemplate(templateName);
        return processTemplate(template, variables);
    }
}