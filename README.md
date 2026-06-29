# Desafio Técnico Engeman - Backend

API REST para gerenciamento de imóveis com autenticação JWT, desenvolvida como desafio técnico para a Engeman.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Como Rodar](#como-rodar)
- [Autenticação](#autenticação)
- [Documentação Swagger](#documentação-swagger)
- [Modelos de Dados](#modelos-de-dados)
- [Controle de Acesso por Papel (RBAC)](#controle-de-acesso-por-papel-rbac)
- [Testes Unitários](#testes-unitários)

---

## Tecnologias

| Tecnologia              | Versão | Uso                                   |
|-------------------------|--------|---------------------------------------|
| Java                    | 21     | Linguagem principal                   |
| Spring Boot             | 4.1.0  | Framework base                        |
| Spring Security         | —      | Autenticação e autorização            |
| Spring Data JPA         | —      | Persistência de dados                 |
| JJWT                    | 0.12.6 | Geração e validação de tokens JWT     |
| PostgreSQL              | 16     | Banco de dados principal              |
| H2                      | —      | Banco em memória para testes          |
| Lombok                  | —      | Redução de boilerplate                |
| SpringDoc OpenAPI       | 3.0.2  | Documentação Swagger                  |
| Docker / Docker Compose | —      | Containerização                       |
| Maven                   | —      | Build e gerenciamento de dependências |

---

## Estrutura do Projeto

```
src/
└── main/java/com/luanmvcosta0/desafio_tecnico_engeman_be/
    ├── Application.java
    ├── modules/
    │   ├── property/
    │   │   ├── controller/   PropertyController.java
    │   │   ├── dtos/         PropertyDto.java
    │   │   ├── enums/        Type.java
    │   │   ├── model/        PropertyEntity.java
    │   │   ├── repository/   PropertyRepository.java
    │   │   └── service/      PropertyService.java
    │   └── user/
    │       ├── auth/
    │       │   ├── config/   SecurityConfig.java
    │       │   └── filter/   JwtAuthenticationFilter.java
    │       ├── controller/   AuthController.java
    │       ├── dtos/
    │       │   ├── request/  UserLoginRequestDto.java, UserRegisterRequestDto.java
    │       │   └── response/ UserLoginResponseDto.java, UserRegisterResponseDto.java
    │       ├── enums/        UserRole.java
    │       ├── model/        UserEntity.java
    │       ├── repository/   UserRepository.java
    │       └── service/      AuthService.java, JwtService.java, UserDetailsServiceImpl.java
    └── swagger/
        └── SwaggerConfig.java
```

---

## Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e preencha os valores:

```bash
cp .env.example .env
```

| Variável            | Descrição                                          | Exemplo                              |
|---------------------|----------------------------------------------------|--------------------------------------|
| `POSTGRES_DB`       | Nome do banco de dados                             | `engeman`                            |
| `POSTGRES_USER`     | Usuário do PostgreSQL                              | `postgres`                           |
| `POSTGRES_PASSWORD` | Senha do PostgreSQL                                | `senha123`                           |
| `DDL_AUTO`          | Estratégia DDL do Hibernate                        | `update` ou `create`                 |
| `JWT_SECRET`        | Chave secreta para assinar o JWT (mínimo 32 chars) | `minha-chave-super-secreta-32-chars` |
| `JWT_EXPIRATION`    | Tempo de expiração do token em milissegundos       | `86400000` (24h)                     |

### Exemplo de `.env` preenchido

```env
POSTGRES_DB=engeman
POSTGRES_USER=postgres
POSTGRES_PASSWORD=senha123

DDL_AUTO=update

JWT_SECRET=minha-chave-super-secreta-para-jwt-32chars
JWT_EXPIRATION=86400000
```

---

## Como Rodar

O Docker Compose sobe o banco PostgreSQL e a aplicação juntos. A aplicação só inicia após o banco estar saudável.

**Modo desenvolvimento (com hot reload):**

```bash
docker compose up --watch
```

Com `--watch`, mudanças em `./src` reiniciam o container automaticamente. Alterações no `pom.xml` disparam um rebuild completo.

**Modo produção (JAR otimizado):**

```bash
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

---

## Autenticação

A API usa **JWT (JSON Web Token)** com sessão stateless. Não há cookies nem estado no servidor.

### Fluxo

1. Registre um usuário em `POST /auth/register`
2. Faça login em `POST /auth/login` — a resposta contém o token JWT
3. Inclua o token em todas as requisições protegidas via header:

```
Authorization: Bearer <seu-token-aqui>
```

### Geração do Token

O token é gerado pelo `JwtService` com as seguintes informações:

- **Subject:** e-mail do usuário
- **Claim `role`:** papel do usuário (`ADMIN`, `BROKER` ou `CUSTOMER`)
- **Assinatura:** HMAC-SHA com a chave definida em `JWT_SECRET`
- **Expiração:** configurável via `JWT_EXPIRATION`

O `JwtAuthenticationFilter` intercepta cada requisição, extrai o token do header `Authorization`, valida a assinatura e
a expiração, e autentica o usuário no contexto do Spring Security.

### Regra de primeiro cadastro

O **primeiro usuário** registrado no sistema recebe automaticamente o papel `ADMIN`, independente do campo `role`
informado no body.

---

## Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

A documentação lista todos os endpoints, exemplos de request/response e permite testar as chamadas diretamente pelo
navegador. Para testar rotas protegidas, clique em **Authorize** e informe o token no formato:

```
Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Modelos de Dados

### UserEntity

| Campo      | Tipo            | Descrição                                  |
|------------|-----------------|--------------------------------------------|
| `id`       | `string` (UUID) | Identificador único gerado automaticamente |
| `username` | `string`        | Nome de exibição do usuário                |
| `email`    | `string`        | E-mail (usado como login)                  |
| `password` | `string`        | Senha encriptada com BCrypt                |
| `role`     | `UserRole`      | Papel do usuário no sistema                |

### PropertyEntity

| Campo    | Tipo            | Descrição                                  |
|----------|-----------------|--------------------------------------------|
| `id`     | `string` (UUID) | Identificador único gerado automaticamente |
| `name`   | `string`        | Nome do imóvel                             |
| `rooms`  | `integer`       | Número de quartos                          |
| `price`  | `BigDecimal`    | Preço do imóvel                            |
| `type`   | `Type`          | Tipo do imóvel                             |
| `active` | `boolean`       | Se o imóvel está ativo (padrão: `true`)    |

### Enums

**UserRole:**
| Valor | Descrição |
|---|---|
| `ADMIN` | Acesso total. Atribuído automaticamente ao primeiro usuário cadastrado |
| `BROKER` | Corretor. Pode criar, editar e desativar imóveis, e visualizar listagens |
| `CUSTOMER` | Cliente. Acesso somente leitura às propriedades |

**Type:**
| Valor | Descrição |
|---|---|
| `HOUSE` | Casa |
| `CONDOMINIUM` | Condomínio |
| `BUILDING` | Prédio/Edifício |

---

## Controle de Acesso por Papel (RBAC)

| Operação             | `ADMIN` | `BROKER` | `CUSTOMER` |
|----------------------|:-------:|:--------:|:----------:|
| Registrar/Login      |  Livre  |  Livre   |   Livre    |
| `GET /property/**`   |    ✅    |    ✅     |     ✅      |
| `POST /property/**`  |    ✅    |    ✅     |     ❌      |
| `PUT /property/**`   |    ✅    |    ✅     |     ❌      |
| `PATCH /property/**` |    ✅    |    ✅     |     ❌      |

---

## Testes Unitários

Os testes usam **JUnit 5**, **Mockito** e **MockMvc**, com banco **H2 em memória** — sem necessidade do PostgreSQL rodando.

### Executar via Docker

```bash
docker build --target test .
```

Se todos os testes passarem, o build termina com `FINISHED` sem erros. Se algum falhar, o build para com `BUILD FAILURE` e exibe qual teste quebrou.

Para ver o log completo do Maven no terminal:

```bash
docker build --target test --progress=plain --no-cache .
```

O `--no-cache` é necessário para forçar a reexecução dos testes, já que o Docker cacheia o resultado do build anterior.

### Cobertura dos testes

| Classe testada            | Arquivo de teste              |
|---------------------------|-------------------------------|
| `AuthController`          | `AuthControllerTest`          |
| `AuthService`             | `AuthServiceTest`             |
| `JwtService`              | `JwtServiceTest`              |
| `UserDetailsServiceImpl`  | `UserDetailsServiceImplTest`  |
| `JwtAuthenticationFilter` | `JwtAuthenticationFilterTest` |
| `UserRepository`          | `UserRepositoryTest`          |
| `PropertyController`      | `PropertyControllerTest`      |
| `PropertyService`         | `PropertyServiceTest`         |
| `PropertyRepository`      | `PropertyRepositoryTest`      |

Os testes de repositório usam `@DataJpaTest` com H2. Os de controller usam `@WebMvcTest` com `MockMvc`. Os de service usam `@ExtendWith(MockitoExtension.class)` com mocks das dependências.
