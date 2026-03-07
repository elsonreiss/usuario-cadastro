# 🎨 ARQUITETURA DO PROJETO - Diagrama Visual

## 📐 VISÃO GERAL DA ARQUITETURA

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Navegador/App)                      │
│                    Faz requisições HTTP REST                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER (Futuro)                        │
│                                                                     │
│  @RestController                                                    │
│  @RequestMapping("/api/users")                                      │
│                                                                     │
│  Responsabilidades:                                                 │
│  • Receber requisições HTTP (GET, POST, PUT, DELETE)               │
│  • Validar JSON de entrada                                         │
│  • Chamar o Service                                                 │
│  • Retornar resposta HTTP                                          │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER ⭐ (Atual)                       │
│                                                                     │
│  @Service                                                           │
│  @Transactional                                                     │
│  @RequiredArgsConstructor                                           │
│  @Slf4j                                                             │
│                                                                     │
│  Responsabilidades:                                                 │
│  • Lógica de negócio                                               │
│  • Validações de regras                                            │
│  • Gerenciamento de transações                                     │
│  • Logging de operações                                            │
│                                                                     │
│  Métodos:                                                           │
│  • createUser(User)      → Cria novo usuário                       │
│  • updateUser(Long, User)→ Atualiza usuário                        │
│  • findAll()             → Lista todos                             │
│  • findById(Long)        → Busca por ID                            │
│  • findByEmail(String)   → Busca por email                         │
│  • deleteById(Long)      → Deleta usuário                          │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER ⭐ (Atual)                      │
│                                                                     │
│  public interface UserRepository extends JpaRepository             │
│                                                                     │
│  Responsabilidades:                                                 │
│  • Acesso ao banco de dados                                        │
│  • Queries automáticas do Spring Data JPA                         │
│                                                                     │
│  Métodos Herdados:                                                  │
│  • save()           → INSERT/UPDATE                                │
│  • findById()       → SELECT por ID                                │
│  • findAll()        → SELECT todos                                 │
│  • deleteById()     → DELETE                                       │
│  • existsById()     → Verifica existência                          │
│                                                                     │
│  Métodos Customizados:                                              │
│  • findByEmail()    → SELECT por email                             │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      ENTITY LAYER ⭐ (Atual)                        │
│                                                                     │
│  @Entity                                                            │
│  @Table(name = "tb_users")                                          │
│  @Getter @Setter                                                    │
│  @NoArgsConstructor @AllArgsConstructor                             │
│  @Builder                                                           │
│                                                                     │
│  Campos:                                                            │
│  • id: Long          → Chave primária (auto increment)             │
│  • name: String      → Nome do usuário                             │
│  • email: String     → Email (único)                               │
│  • password: String  → Senha (em produção: criptografada)          │
│                                                                     │
│  Validações:                                                        │
│  • @NotBlank        → Não pode ser vazio                           │
│  • @Email           → Formato de email válido                      │
│  • @Size            → Tamanho mínimo/máximo                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      DATABASE (PostgreSQL/MySQL)                    │
│                                                                     │
│  Tabela: tb_users                                                   │
│  ┌────────┬──────────────┬───────────┬──────────┐                  │
│  │ id     │ name         │ email     │ password │                  │
│  ├────────┼──────────────┼───────────┼──────────┤                  │
│  │ 1      │ João Silva   │ joao@...  │ ******** │                  │
│  │ 2      │ Maria Santos │ maria@... │ ******** │                  │
│  └────────┴──────────────┴───────────┴──────────┘                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 FLUXO DE CRIAÇÃO DE USUÁRIO

### Exemplo: POST /api/users (Quando você criar o Controller)

```
1️⃣ CLIENTE envia JSON:
   POST /api/users
   {
     "name": "João Silva",
     "email": "joao@email.com",
     "password": "123456"
   }
   
   │
   ▼

2️⃣ CONTROLLER recebe e valida:
   • Verifica se JSON é válido
   • Valida @NotBlank, @Email, @Size
   • Chama: userService.createUser(user)
   
   │
   ▼

3️⃣ SERVICE executa lógica de negócio:
   • log.info("Tentando criar usuário...")
   • Verifica se email já existe
     └─> Se existir: LANÇA EXCEÇÃO ❌
   • Se não existir: Salva usuário
   • log.info("Usuário criado com sucesso")
   
   │
   ▼

4️⃣ REPOSITORY acessa o banco:
   • Spring Data JPA gera SQL automaticamente
   • SQL: INSERT INTO tb_users (name, email, password) 
           VALUES ('João Silva', 'joao@email.com', '123456')
   • Retorna User com ID gerado
   
   │
   ▼

5️⃣ DATABASE persiste os dados:
   • Valida constraints (UNIQUE, NOT NULL)
   • Gera ID (auto increment)
   • Salva no disco
   
   │
   ▼

6️⃣ RESPOSTA volta pela cadeia:
   Repository → Service → Controller → Cliente
   
   HTTP 201 CREATED
   {
     "id": 1,
     "name": "João Silva",
     "email": "joao@email.com"
   }
```

---

## 🎯 RESPONSABILIDADES DE CADA CAMADA

### 🎨 **Controller (Futuro)**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.createUser(user));
    }
}
```
**Faz:**
- ✅ Recebe requisições HTTP
- ✅ Valida entrada (JSON)
- ✅ Retorna códigos HTTP corretos (200, 201, 400, 404, 500)

**NÃO faz:**
- ❌ Lógica de negócio
- ❌ Acesso ao banco

---

### 🧠 **Service (Atual)**
```java
@Service
@Transactional(readOnly = true)
public class UserService {
    
    @Transactional
    public User createUser(User user) {
        // Valida email único
        // Salva usuário
        // Registra logs
    }
}
```
**Faz:**
- ✅ Lógica de negócio
- ✅ Validações de regras
- ✅ Orquestração de operações
- ✅ Transações
- ✅ Logging

**NÃO faz:**
- ❌ Lidar com HTTP
- ❌ SQL direto

---

### 💾 **Repository (Atual)**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```
**Faz:**
- ✅ Acesso ao banco de dados
- ✅ Queries automáticas
- ✅ Abstração de SQL

**NÃO faz:**
- ❌ Lógica de negócio
- ❌ Validações

---

### 📦 **Entity (Atual)**
```java
@Entity
@Table(name = "tb_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```
**Faz:**
- ✅ Representa tabela do banco
- ✅ Mapeia campos
- ✅ Validações básicas (@NotBlank, @Email)

**NÃO faz:**
- ❌ Lógica de negócio
- ❌ Acesso ao banco

---

## 🎓 BENEFÍCIOS DA ARQUITETURA EM CAMADAS

### ✅ **Separação de Responsabilidades**
Cada camada tem uma função específica e bem definida.

### ✅ **Facilita Testes**
```java
// Você pode testar o Service SEM banco de dados!
@Test
void testCreateUser() {
    UserRepository mockRepo = mock(UserRepository.class);
    UserService service = new UserService(mockRepo);
    
    when(mockRepo.findByEmail("test@email.com"))
        .thenReturn(Optional.empty());
    
    User user = service.createUser(newUser);
    assertNotNull(user);
}
```

### ✅ **Facilita Manutenção**
- Mudar o banco? Só mexe no Repository
- Mudar regra de negócio? Só mexe no Service
- Mudar formato de resposta? Só mexe no Controller

### ✅ **Reutilização**
O mesmo Service pode ser usado por:
- REST API (Controller)
- GraphQL
- SOAP
- Scheduled Tasks
- Mensageria (RabbitMQ, Kafka)

---

## 📊 PADRÕES APLICADOS

### 🎨 **Dependency Injection**
```java
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; // Injetado pelo Spring
}
```

### 🎨 **Repository Pattern**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring cria a implementação automaticamente
}
```

### 🎨 **Builder Pattern**
```java
User user = User.builder()
    .name("João")
    .email("joao@email.com")
    .password("123456")
    .build();
```

### 🎨 **Optional Pattern**
```java
Optional<User> userOpt = userRepository.findByEmail(email);
userOpt.ifPresent(user -> System.out.println(user.getName()));
```

---

## 🚀 PRÓXIMO PASSO: CRIAR O CONTROLLER

```java
package com.elsonreiss.create_user.controller;

import com.elsonreiss.create_user.model.User;
import com.elsonreiss.create_user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

**Agora você entende a arquitetura completa! 🎉**

