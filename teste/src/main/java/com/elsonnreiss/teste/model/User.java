package com.elsonnreiss.teste.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "o nome não pode ser vazio")
    @Size(min = 1, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "o email não pode ser vazio")
    @Email(message = "o email precisa ser valido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "a senha não pode ser vazia")
    @Size(min = 6, message = "a senha deve ter no minimo 6 caracteres")
    @Column(nullable = false, length = 40)
    private String password;
}
