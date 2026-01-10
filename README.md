# Microserviço de Envio de Email

Microserviço desenvolvido em Spring Boot para envio de emails via SMTP, com persistência do histórico de envios em banco de dados PostgreSQL e processamento assíncrono de mensagens via RabbitMQ.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.9
- Spring Data JPA
- Spring Mail
- Spring AMQP (RabbitMQ)
- PostgreSQL
- Flyway (Migrations)
- Lombok
- Maven

## Funcionalidades

- Envio de emails via SMTP (configurado para Gmail)
- Processamento assíncrono de emails através de fila RabbitMQ
- Registro do histórico de envios no banco de dados
- Validação de dados de entrada
- Controle de status de envio (SENT/ERROR)
- Registro de data/hora de envio
- Migrations automáticas do banco de dados com Flyway

## Arquitetura

O microserviço suporta dois modos de operação:

1. **Síncrono**: Através do endpoint REST `/sending-email`
2. **Assíncrono**: Através de mensagens RabbitMQ consumidas da fila configurada

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
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
SPRING_RABBITMQ_QUEUE=ms.email
```

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

### 2. Inicie o RabbitMQ
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 3. Configure as variáveis de ambiente
```bash
# Configure conforme o ambiente (local/prod)
```

### 4. Execute o projeto
```bash
# Usando Maven
./mvnw spring-boot:run

# Ou compile e execute o JAR
./mvnw clean package
java -jar target/email-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### POST /sending-email

Envia um email de forma síncrona e registra o envio no banco de dados.

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
  "id": "550e8400-e29b-41d4-a716-446655440000",
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

## Integração via RabbitMQ

### Publicando Mensagens na Fila

Para enviar emails de forma assíncrona, publique uma mensagem na fila configurada (padrão: `ms.email`) com o seguinte formato JSON:

```json
{
  "ownerRef": "identificador-do-proprietario",
  "emailFrom": "remetente@example.com",
  "emailTo": "destinatario@example.com",
  "subject": "Assunto do email",
  "text": "Corpo do email"
}
```

O consumer irá processar a mensagem automaticamente e enviar o email.

### Exemplo com N8N

1. Configure um nó RabbitMQ no N8N
2. Defina a exchange e routing key conforme sua configuração
3. Envie o payload no formato especificado acima

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/sistema/
│   │   ├── configurations/  # Configurações (RabbitMQ)
│   │   ├── consumers/       # Consumers RabbitMQ
│   │   ├── controllers/     # Controllers REST
│   │   ├── dtos/            # Data Transfer Objects
│   │   ├── enums/           # Enumerações
│   │   ├── models/          # Entidades JPA
│   │   ├── repositories/    # Repositórios JPA
│   │   ├── services/        # Lógica de negócio
│   │   └── Startup.java     # Classe principal
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       └── db/migration/    # Scripts Flyway (se houver)
└── test/                    # Testes unitários
```

## Banco de Dados

### Tabela: tb_email

| Campo           | Tipo         | Descrição                      |
|-----------------|--------------|--------------------------------|
| id              | UUID         | ID único (PK)                  |
| owner_ref       | VARCHAR      | Referência do proprietário     |
| email_from      | VARCHAR      | Email remetente                |
| email_to        | VARCHAR      | Email destinatário             |
| subject         | VARCHAR      | Assunto do email               |
| text            | TEXT         | Corpo do email                 |
| send_date_email | TIMESTAMP    | Data/hora do envio             |
| status_email    | VARCHAR      | Status (SENT/ERROR)            |

### Migrations com Flyway

O Flyway está configurado para executar migrations automaticamente no startup da aplicação. Para criar novas migrations:

1. Crie arquivos SQL em `src/main/resources/db/migration/`
2. Nomeie seguindo o padrão: `V{versão}__{descrição}.sql`
   - Exemplo: `V1__create_email_table.sql`
3. As migrations serão executadas automaticamente na próxima inicialização

**Observação**: A configuração atual possui `spring.flyway.clean-disabled=false`, o que é apropriado apenas para ambientes de desenvolvimento/estudos.

## Configuração do Gmail

Para usar o Gmail como servidor SMTP:

1. Acesse sua conta Google
2. Ative a verificação em duas etapas
3. Gere uma "Senha de app" em: https://myaccount.google.com/apppasswords
4. Use essa senha na variável `MAILPASSWORD`

## Monitoramento

### RabbitMQ Management Console

Acesse `http://localhost:15672` para monitorar:
- Filas e mensagens
- Consumers ativos
- Taxa de processamento
- Mensagens não processadas

**Credenciais padrão**: guest/guest

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## Licença

Este projeto é de código aberto e está disponível sob a licença especificada no repositório.

## Contato

Para dúvidas ou sugestões, abra uma issue no GitHub.