# Microserviço de Envio de Email

Microserviço desenvolvido em Spring Boot para envio de emails via SMTP, com persistência do histórico de envios em banco de dados PostgreSQL.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.9
- Spring Data JPA
- Spring Mail
- PostgreSQL
- Lombok
- Maven
- Swagger/OpenAPI (SpringDoc)
- LOG4J2
- Flyway (Migrations)
- RabbitMQ (Mensageria)

## Funcionalidades

- Envio de emails via SMTP (configurado para Gmail)
- Registro do histórico de envios no banco de dados
- Validação de dados de entrada
- Controle de status de envio (SENT/ERROR)
- Registro de data/hora de envio
- Documentação da API com Swagger/OpenAPI
- Sistema de logs estruturado com LOG4J2
- Gerenciamento de migrations com Flyway
- Integração com RabbitMQ para processamento assíncrono de mensagens

## Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- PostgreSQL 12+
- RabbitMQ 3.8+
- Conta Gmail com senha de aplicativo configurada

## Configuração

### Variáveis de Ambiente (Produção)

Configure as seguintes variáveis de ambiente para o profile `prod`:

```properties
# Servidor
PORT=8080

# PostgreSQL
PGHOST=seu-host-postgresql
PGPORT=5432
PGDATABASE=nome-do-banco
PGUSER=usuario
PGPASSWORD=senha

# JWT (se necessário para autenticação futura)
JWTSECRETKEY=sua-chave-secreta

# SMTP Gmail
MAILHOST=smtp.gmail.com
MAILPORT=587
MAILUSERNAME=seu-email@gmail.com
MAILPASSWORD=sua-senha-de-aplicativo

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
```

## Flyway - Gerenciamento de Migrations

O microserviço utiliza **Flyway** para versionamento e controle de migrations do banco de dados.

### Configuração do Flyway

O Flyway está configurado para:
- Executar migrations automaticamente no startup da aplicação
- Criar/atualizar tabelas conforme os scripts de migration
- Ignorar placeholders `${...}` nos scripts SQL

### Estrutura de Migrations

Crie os scripts de migration em: `src/main/resources/db/migration/`

Padrão de nomenclatura: `V{versao}__{descricao}.sql`

Exemplo:
```
db/migration/
├── V1__create_table_email.sql
```

### Exemplo de Migration

**V1__create_table_email.sql:**
```sql
CREATE TABLE tb_email (
    id BIGSERIAL PRIMARY KEY,
    owner_ref VARCHAR(255) NOT NULL,
    email_from VARCHAR(255) NOT NULL,
    email_to VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    send_date_email TIMESTAMP,
    status_email VARCHAR(50)
);

CREATE INDEX idx_email_status ON tb_email(status_email);
CREATE INDEX idx_email_send_date ON tb_email(send_date_email);
```

### Comandos Úteis do Flyway

**ATENÇÃO:** O comando `clean` está habilitado apenas para estudos/desenvolvimento. Em produção, desabilite com:
```properties
spring.flyway.clean-disabled=true
```

## RabbitMQ - Mensageria Assíncrona

O microserviço está preparado para integração com **RabbitMQ** para processamento assíncrono de envio de emails.

### Arquitetura de Mensageria

- **Producer:** Outros microserviços enviam mensagens para a fila
- **Consumer:** Este microserviço consome mensagens e processa o envio
- **Dead Letter Queue (DLQ):** Mensagens com falha são redirecionadas

### Filas Configuradas

- **email.queue:** Fila principal para envio de emails
- **email.dlq:** Dead Letter Queue para emails com erro

### Exemplo de Payload da Mensagem

```json
{
  "ownerRef": "user-123",
  "emailFrom": "noreply@sistema.com",
  "emailTo": "usuario@example.com",
  "subject": "Bem-vindo ao sistema",
  "text": "Obrigado por se cadastrar!"
}
```

### Configurando o RabbitMQ

1. **Instalar o RabbitMQ:**
```bash
# Docker
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# Acessar console: http://localhost:15672
# Usuário padrão: guest / guest
```

2. **Criar Exchange e Filas:**
   - Exchange: `email.exchange` (tipo: direct)
   - Queue: `email.queue`
   - Routing Key: `email.routing.key`

### Monitoramento

Acesse o RabbitMQ Management Console:
- URL: `http://localhost:15672`
- Usuário: `guest`
- Senha: `guest`

### Profile Ativo

O microserviço utiliza profiles do Spring. Por padrão, está configurado para usar o profile `local`.

Para produção, defina:
```bash
export SPRING_PROFILES_ACTIVE=prod
```

## Instalação e Execução

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd ms-email
```

### 2. Configure as variáveis de ambiente
```bash
# Configure conforme o ambiente (local/prod)
```

### 3. Execute o projeto
```bash
# Usando Maven
./mvnw spring-boot:run

# Ou compile e execute o JAR
./mvnw clean package
java -jar target/email-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### POST /sending-email

Envia um email e registra o envio no banco de dados.

**Request Body:**
```json
{
  "ownerRef": "identificador-do-proprietario",
  "emailFrom": "remetente@example.com",
  "emailTo": "destinatario@example.com",
  "subject": "Assunto do email",
  "text": "Corpo do email"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "ownerRef": "identificador-do-proprietario",
  "emailFrom": "remetente@example.com",
  "emailTo": "destinatario@example.com",
  "subject": "Assunto do email",
  "text": "Corpo do email",
  "sendDateEmail": "2025-01-09T10:30:00",
  "statusEmail": "SENT"
}
```

**Validações:**
- Todos os campos são obrigatórios
- `emailFrom` e `emailTo` devem ser emails válidos

## Documentação da API (Swagger)

A documentação interativa da API está disponível através do Swagger UI.

### Acessando o Swagger

Após iniciar a aplicação, acesse:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **API Docs (JSON):** `http://localhost:8080/api-docs`

### Configuração do Swagger

O Swagger está configurado com:
- Ordenação de operações por método HTTP
- Ordenação de tags em ordem alfabética
- Filtro de endpoints habilitado
- Funcionalidade "Try it out" ativada

Para desabilitar o Swagger em produção, descomente as seguintes linhas no `application-prod.properties`:
```properties
springdoc.api-docs.enabled=false
springdoc.swagger-ui.enabled=false
```

## Sistema de Logs

O microserviço utiliza **LOG4J2** para gerenciamento de logs estruturados.

### Níveis de Log

- **ERROR:** Erros no envio de emails e exceções críticas
- **WARN:** Avisos e situações anormais
- **INFO:** Informações sobre envios de email e inicialização
- **DEBUG:** Detalhes de execução (apenas em desenvolvimento)

### Configuração de Logs

Os logs podem ser configurados através do arquivo `log4j2.xml` (não incluído no repositório, criar conforme necessidade).

Exemplo de configuração básica `log4j2.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
        </Console>
        <RollingFile name="RollingFile" fileName="logs/ms-email.log"
                     filePattern="logs/ms-email-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy interval="1"/>
                <SizeBasedTriggeringPolicy size="10MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Root>
        <Logger name="br.com.sistema" level="debug" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Logger>
    </Loggers>
</Configuration>
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/sistema/
│   │   ├── controllers/     # Controllers REST
│   │   ├── dtos/            # Data Transfer Objects
│   │   ├── enums/           # Enumerações
│   │   ├── models/          # Entidades JPA
│   │   ├── repositories/    # Repositórios JPA
│   │   ├── services/        # Lógica de negócio
│   │   └── Startup.java     # Classe principal
│   └── resources/
│       ├── application.properties
│       └── application-prod.properties
└── test/                    # Testes unitários
```

## Banco de Dados

### Tabela: tb_email

| Campo          | Tipo         | Descrição                      |
|----------------|--------------|--------------------------------|
| id             | BIGINT       | ID auto-incrementado (PK)      |
| owner_ref      | VARCHAR      | Referência do proprietário     |
| email_from     | VARCHAR      | Email remetente                |
| email_to       | VARCHAR      | Email destinatário             |
| subject        | VARCHAR      | Assunto do email               |
| text           | TEXT         | Corpo do email                 |
| send_date_email| TIMESTAMP    | Data/hora do envio             |
| status_email   | VARCHAR      | Status (SENT/ERROR)            |

## Configuração do Gmail

Para usar o Gmail como servidor SMTP:

1. Acesse sua conta Google
2. Ative a verificação em duas etapas
3. Gere uma "Senha de app" em: https://myaccount.google.com/apppasswords
4. Use essa senha na variável `MAILPASSWORD`

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Licença

Este projeto é de código aberto e está disponível sob a licença especificada no repositório.