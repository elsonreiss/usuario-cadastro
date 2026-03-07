package com.elsonreiss.create_user.controller;

import com.elsonreiss.create_user.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users") // Rota base para este controlador via http
@CrossOrigin
public class UserController {

    final UserService userService; // Injeção de dependência do serviço (Lógica de Negócio)

    public UserController(UserService userService) {
        this.userService = userService; // O Spring injetará a implementação do UserService automaticamente
    }

    @PostMapping
}
