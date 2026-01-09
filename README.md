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

## Funcionalidades

- Envio de emails via SMTP (configurado para Gmail)
- Registro do histórico de envios no banco de dados
- Validação de dados de entrada
- Controle de status de envio (SENT/ERROR)
- Registro de data/hora de envio

## Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- PostgreSQL 12+
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

## Contato

Para dúvidas ou sugestões, abra uma issue no GitHub.