package com.elsonnreiss.teste.controller;

import com.elsonnreiss.teste.dto.request.UserRequest;
import com.elsonnreiss.teste.dto.response.UserResponse;
import com.elsonnreiss.teste.model.User;
import com.elsonnreiss.teste.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        User user = service.createUser(userRequest);
        return ResponseEntity.status(201).body(user);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        List<UserResponse> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        UserResponse userId = service.findById(id);
        return ResponseEntity.ok(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User updateUser = service.userUpdadte(id, userRequest);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok("Deletado com sucesso");
    }
}
