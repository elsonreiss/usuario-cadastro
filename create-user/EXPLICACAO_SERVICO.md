# 📚 GUIA DE ESTUDO - Service Layer (UserService)

## 🎯 O QUE FOI MELHORADO NA CLASSE

### ✅ 1. **Anotações Lombok**
```java
@RequiredArgsConstructor
```
- **O que faz:** Gera automaticamente um construtor com todos os campos `final`
- **Vantagem:** Reduz código boilerplate (código repetitivo)
- **Equivalente manual:**
```java
public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
}
```

```java
@Slf4j
```
- **O que faz:** Cria automaticamente um logger chamado `log`
- **Uso:** `log.info()`, `log.error()`, `log.warn()`
- **Vantagem:** Não precisa escrever `Logger log = LoggerFactory.getLogger(...)`

---

### ✅ 2. **Gestão de Transações**
```java
@Transactional(readOnly = true)  // Na classe
@Transactional                   // Nos métodos de escrita
```

**Por que isso importa:**
- **readOnly = true**: Otimiza operações de leitura (SELECT)
- **@Transactional sem readOnly**: Permite escrita (INSERT, UPDATE, DELETE)
- **ACID**: Garante que operações sejam Atômicas, Consistentes, Isoladas e Duráveis

**Exemplo:**
```java
@Transactional // Se der erro, ROLLBACK automático!
public User createUser(User user) {
    // Se qualquer linha falhar, NADA é salvo no banco
    userRepository.save(user);
}
```

---

### ✅ 3. **Uso de Optional**
```java
Optional<User> findByEmail(String email)
```

**Por que usar Optional?**
- Evita `NullPointerException`
- Força você a tratar o caso de "não encontrado"

**Métodos úteis do Optional:**
```java
// 1. ifPresent() - Executa código SE existir
userRepository.findByEmail(email)
    .ifPresent(user -> {
        // Só executa se encontrar o usuário
        System.out.println("Usuário existe!");
    });

// 2. orElseThrow() - Lança exceção SE NÃO existir
User user = userRepository.findById(id)
    .orElseThrow(() -> new IllegalArgumentException("Não encontrado"));

// 3. orElse() - Retorna valor padrão SE NÃO existir
User user = userRepository.findByEmail(email)
    .orElse(new User("Padrão", "padrao@email.com", "123456"));

// 4. isPresent() - Verifica se existe
if (optional.isPresent()) {
    User user = optional.get();
}
```

---

### ✅ 4. **Logging Estratégico**
```java
log.info("Tentando criar usuário com email: {}", user.getEmail());
log.warn("Email duplicado: {}", user.getEmail());
log.error("Usuário não encontrado. ID: {}", id);
```

**Níveis de Log:**
- **DEBUG**: Informações técnicas detalhadas (desenvolvimento)
- **INFO**: Eventos normais do sistema
- **WARN**: Avisos de situações anormais (mas não críticas)
- **ERROR**: Erros que impedem operações

**Por que usar `{}` ao invés de `+`?**
```java
// ❌ RUIM - Concatena sempre, mesmo se log estiver desabilitado
log.info("Email: " + user.getEmail());

// ✅ BOM - Só concatena se log estiver habilitado (performance!)
log.info("Email: {}", user.getEmail());
```

---

### ✅ 5. **Validações de Negócio**

#### **Validação: Email Único**
```java
userRepository.findByEmail(user.getEmail())
    .ifPresent(existingUser -> {
        throw new IllegalArgumentException("Email já cadastrado");
    });
```
**O que acontece:**
1. Busca no banco por email
2. Se encontrar (`ifPresent`), lança exceção
3. Se não encontrar, continua a execução

#### **Validação: Usuário Existe**
```java
User existingUser = userRepository.findById(id)
    .orElseThrow(() -> new IllegalArgumentException("Não encontrado"));
```
**O que acontece:**
1. Busca usuário por ID
2. Se encontrar, retorna o usuário
3. Se não encontrar, lança exceção

---

### ✅ 6. **Padrão de Atualização Seguro**
```java
// ❌ INSEGURO - Poderia sobrescrever o ID!
userRepository.save(user);

// ✅ SEGURO - Só atualiza campos específicos
existingUser.setName(user.getName());
existingUser.setEmail(user.getEmail());
existingUser.setPassword(user.getPassword());
userRepository.save(existingUser);
```

**Por que isso é importante:**
- Evita que o cliente passe um ID diferente e sobrescreva outro registro
- Controla exatamente quais campos podem ser atualizados

---

## 🔄 FLUXO DE UMA REQUISIÇÃO (Arquitetura em Camadas)

```
┌─────────────────────────────────────────────────────┐
│  1. CONTROLLER (HTTP Layer)                        │
│  - Recebe requisição HTTP                          │
│  - Valida formato JSON                             │
│  - Chama o Service                                 │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│  2. SERVICE (Business Logic Layer)                 │
│  - Valida regras de negócio                        │
│  - Orquestra operações                             │
│  - Chama o Repository                              │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│  3. REPOSITORY (Data Access Layer)                 │
│  - Acessa o banco de dados                         │
│  - Executa SQL gerado pelo Spring Data JPA        │
└─────────────┬───────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────┐
│  4. DATABASE (PostgreSQL, MySQL, etc.)             │
│  - Armazena os dados                               │
└─────────────────────────────────────────────────────┘
```

---

## 🎓 CONCEITOS IMPORTANTES PARA ESTUDAR

### **1. Injeção de Dependência (Dependency Injection)**
```java
private final UserRepository userRepository;
```
- O Spring cria e injeta automaticamente a instância
- Facilita testes (você pode injetar um mock)
- Reduz acoplamento entre classes

### **2. Programação Funcional com Lambda**
```java
.ifPresent(user -> {
    // Lambda expression
    throw new IllegalArgumentException("Erro");
});
```
- Sintaxe moderna do Java 8+
- Código mais conciso e legível

### **3. Method Reference**
```java
// Ao invés de:
.orElseThrow(() -> new IllegalArgumentException("Erro"));

// Pode usar (mais avançado):
.orElseThrow(IllegalArgumentException::new);
```

### **4. Exception Handling**
- `IllegalArgumentException`: Para erros de validação
- `RuntimeException`: Não precisa declarar `throws`
- Em produção, criar exceções customizadas:
  ```java
  public class EmailAlreadyExistsException extends RuntimeException {
      public EmailAlreadyExistsException(String message) {
          super(message);
      }
  }
  ```

---

## 🚀 PRÓXIMOS PASSOS PARA MELHORAR

### **1. Criptografia de Senha (BCrypt)**
```java
// Adicionar dependência Spring Security
private final PasswordEncoder passwordEncoder;

public User createUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
}
```

### **2. DTOs (Data Transfer Objects)**
```java
// Separar objeto de entrada/saída do objeto de domínio
public record UserCreateDTO(String name, String email, String password) {}
public record UserResponseDTO(Long id, String name, String email) {}
```

### **3. Exceções Customizadas**
```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuário não encontrado com ID: " + id);
    }
}
```

### **4. Paginação**
```java
public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
}
```

---

## 📝 EXERCÍCIOS PARA PRATICAR

1. **Crie um Controller** que use este Service
2. **Adicione validação**: Nome deve ter no mínimo 3 caracteres
3. **Implemente busca por nome**: `findByNameContaining(String name)`
4. **Adicione paginação** no método `findAll()`
5. **Crie testes unitários** com Mockito

---

## 🔗 RECURSOS PARA ESTUDAR

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Lombok**: https://projectlombok.org/
- **Java Optional**: https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
- **Transaction Management**: https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction

---

**Boa sorte nos estudos! 🎓**

