# 📚 GUIA DE ESTUDO - Entidades JPA (User Entity)

## 🎯 ENTENDENDO AS ANOTAÇÕES DO JPA

### **@Entity**
```java
@Entity
public class User { }
```
- **O que faz:** Marca a classe como uma entidade JPA (tabela no banco)
- **Resultado:** Spring cria uma tabela automaticamente no banco

---

### **@Table**
```java
@Table(name = "tb_users")
```
- **O que faz:** Define o nome da tabela no banco
- **Sem isso:** Tabela se chamaria `user` (nome da classe)
- **Com isso:** Tabela se chama `tb_users`

**Opções úteis:**
```java
@Table(
    name = "tb_users",
    uniqueConstraints = @UniqueConstraint(columnNames = "email"),
    indexes = @Index(columnList = "email")
)
```

---

### **@Id**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**@Id:** Marca o campo como chave primária (PRIMARY KEY)

**@GeneratedValue:** Define como o ID será gerado

**Estratégias de Geração:**
```java
// 1. IDENTITY - Auto increment do banco (MySQL, PostgreSQL)
@GeneratedValue(strategy = GenerationType.IDENTITY)

// 2. SEQUENCE - Usa sequence do banco (PostgreSQL, Oracle)
@GeneratedValue(strategy = GenerationType.SEQUENCE)

// 3. AUTO - Escolhe automaticamente
@GeneratedValue(strategy = GenerationType.AUTO)

// 4. UUID - Gera ID único universal
@GeneratedValue(strategy = GenerationType.UUID)
```

---

### **@Column**
```java
@Column(nullable = false, unique = true, length = 100)
private String email;
```

**Opções importantes:**
- **nullable = false**: Campo obrigatório (NOT NULL)
- **unique = true**: Valor único no banco (UNIQUE constraint)
- **length = 100**: Tamanho máximo VARCHAR(100)
- **columnDefinition**: SQL customizado
  ```java
  @Column(columnDefinition = "TEXT")
  private String description;
  ```

---

## 🎨 ANOTAÇÕES LOMBOK

### **@Getter e @Setter**
```java
@Getter
@Setter
public class User {
    private String name; // Gera getName() e setName()
}
```

**Equivalente manual:**
```java
public String getName() {
    return this.name;
}

public void setName(String name) {
    this.name = name;
}
```

---

### **@NoArgsConstructor**
```java
@NoArgsConstructor
public class User { }
```

**O que gera:**
```java
public User() {
}
```

**Por que é importante no JPA:**
- O JPA PRECISA de um construtor vazio para criar instâncias
- Sem isso, você terá erro em tempo de execução

---

### **@AllArgsConstructor**
```java
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
}
```

**O que gera:**
```java
public User(Long id, String name, String email, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
}
```

---

### **@Builder**
```java
@Builder
public class User { }
```

**Padrão de projeto Builder:**
```java
// Ao invés de:
User user = new User(null, "João", "joao@email.com", "123456");

// Você pode fazer:
User user = User.builder()
    .name("João")
    .email("joao@email.com")
    .password("123456")
    .build();
```

**Vantagens:**
- Código mais legível
- Ordem dos parâmetros não importa
- Pode omitir campos opcionais

---

## ✅ ANOTAÇÕES DE VALIDAÇÃO (Bean Validation)

### **@NotBlank**
```java
@NotBlank(message = "o nome não pode ser vazio")
private String name;
```

**Valida:**
- Não pode ser `null`
- Não pode ser `""` (string vazia)
- Não pode ser `"   "` (só espaços)

**Diferença entre anotações:**
```java
@NotNull     // Apenas não pode ser null (permite "" e "   ")
@NotEmpty    // Não pode ser null ou "" (permite "   ")
@NotBlank    // Não pode ser null, "" ou "   " (MAIS RIGOROSO)
```

---

### **@Email**
```java
@Email(message = "o email precisa ser válido")
private String email;
```

**Valida:**
- Formato de email válido: `usuario@dominio.com`
- Aceita: `joao@gmail.com`, `maria.silva@empresa.com.br`
- Rejeita: `joao@`, `@gmail.com`, `joao gmail.com`

---

### **@Size**
```java
@Size(min = 6, max = 20, message = "a senha deve ter entre 6 e 20 caracteres")
private String password;
```

**Opções:**
- **min**: Tamanho mínimo
- **max**: Tamanho máximo
- Funciona com: String, Collection, Array

**Outros exemplos:**
```java
@Size(min = 1, max = 100)
private String name;

@Size(min = 11, max = 11) // CPF
private String cpf;
```

---

## 🔐 MELHORIAS DE SEGURANÇA

### **1. Criptografar Senha**
```java
// Nunca armazene senha em texto puro!
// Use BCrypt do Spring Security

@Entity
public class User {
    
    @Column(nullable = false, length = 60) // BCrypt gera 60 caracteres
    private String password;
    
    // No Service:
    public User createUser(User user) {
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }
}
```

---

### **2. Ocultar Senha no JSON**
```java
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
```

**O que faz:**
- **WRITE_ONLY**: Aceita no input (POST/PUT), mas não retorna no output (GET)
- Evita que a senha seja exposta em respostas da API

---

## 🎯 BOAS PRÁTICAS APLICADAS

### **1. Campos Obrigatórios**
```java
@NotBlank(message = "...")  // Validação de aplicação
@Column(nullable = false)    // Validação de banco
```
**Por que duplicar?**
- `@NotBlank`: Valida ANTES de salvar (retorna erro mais rápido)
- `nullable = false`: Garante integridade no banco (segurança extra)

---

### **2. Email Único**
```java
@Column(nullable = false, unique = true)
@Email
@NotBlank
private String email;
```

**Camadas de validação:**
1. `@NotBlank`: Verifica se não está vazio
2. `@Email`: Verifica formato válido
3. `unique = true`: Banco garante unicidade
4. Service verifica duplicidade ANTES de salvar (melhor UX)

---

### **3. Tamanhos Consistentes**
```java
@Size(min = 1, max = 100)        // Validação de aplicação
@Column(nullable = false, length = 100)  // Validação de banco
```

**Por que consistir?**
- Evita erro de "Data too long for column"
- Banco rejeita se passar de 100 caracteres
- Aplicação valida ANTES de tentar salvar

---

## 🧪 EXEMPLO COMPLETO COMENTADO

```java
package com.elsonreiss.create_user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * ENTIDADE USER - Representa um usuário no sistema
 * 
 * Esta classe mapeia a tabela 'tb_users' no banco de dados.
 */
@Entity // 1. Marca como entidade JPA (vira tabela)
@Table(name = "tb_users") // 2. Define nome da tabela
@Getter // 3. Lombok: Gera getters para todos os campos
@Setter // 4. Lombok: Gera setters para todos os campos
@NoArgsConstructor // 5. Lombok: Gera construtor vazio (obrigatório para JPA)
@AllArgsConstructor // 6. Lombok: Gera construtor com todos os campos
@Builder // 7. Lombok: Permite usar User.builder().name("João").build()
public class User {

    // === CHAVE PRIMÁRIA ===
    @Id // Marca como PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto increment
    private Long id;

    // === NOME ===
    @NotBlank(message = "o nome não pode ser vazio") // Validação: não vazio
    @Size(min = 1, max = 100) // Validação: entre 1 e 100 caracteres
    @Column(nullable = false, length = 100) // Banco: NOT NULL, VARCHAR(100)
    private String name;

    // === EMAIL ===
    @NotBlank(message = "o email não pode ser vazio") // Validação: não vazio
    @Email(message = "o email precisa ser válido") // Validação: formato email
    @Column(nullable = false, unique = true) // Banco: NOT NULL, UNIQUE
    private String email;

    // === SENHA ===
    @NotBlank(message = "a senha não pode ser vazia") // Validação: não vazio
    @Size(min = 6, message = "a senha deve ter no mínimo 6 caracteres") // Validação: mínimo 6
    @Column(nullable = false, length = 60) // Banco: NOT NULL, VARCHAR(60) - BCrypt usa 60
    private String password;
}
```

---

## 📊 SQL GERADO PELO JPA

**A classe User acima gera este SQL:**

```sql
CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL
);

CREATE INDEX idx_email ON tb_users(email); -- Por causa do unique
```

---

## 🚀 RELACIONAMENTOS JPA (Para o Futuro)

### **One-to-Many (Um usuário tem vários posts)**
```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> posts;
}

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

---

### **Many-to-Many (Usuário tem várias roles)**
```java
@Entity
public class User {
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}
```

---

## 🎓 EXERCÍCIOS PARA PRATICAR

1. **Adicione campo `createdAt`** com data de criação automática
   ```java
   @CreatedDate
   @Column(nullable = false, updatable = false)
   private LocalDateTime createdAt;
   ```

2. **Adicione campo `active`** para soft delete
   ```java
   @Column(nullable = false)
   private Boolean active = true;
   ```

3. **Crie uma entidade `Address`** com relacionamento `@OneToOne`

4. **Adicione validação de CPF** usando regex
   ```java
   @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
   private String cpf;
   ```

---

## 🔗 RECURSOS PARA ESTUDAR

- **JPA Specification**: https://jakarta.ee/specifications/persistence/
- **Hibernate Docs**: https://hibernate.org/orm/documentation/
- **Bean Validation**: https://beanvalidation.org/
- **Lombok**: https://projectlombok.org/features/

---

**Bons estudos! 🚀**

