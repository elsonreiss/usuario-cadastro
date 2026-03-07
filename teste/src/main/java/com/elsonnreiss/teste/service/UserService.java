package com.elsonnreiss.teste.service;

import com.elsonnreiss.teste.dto.request.UserRequest;
import com.elsonnreiss.teste.dto.response.UserResponse;
import com.elsonnreiss.teste.model.User;
import com.elsonnreiss.teste.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.lang.model.util.Elements;
import java.util.List;

@Service
public class UserService {

    final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(UserRequest request) {

        if(repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já existente");
        }

        User userCreate = new User();
        userCreate.setName(request.getName());
        userCreate.setEmail(request.getEmail());
        userCreate.setPassword(request.getPassword());

        return repository.save(userCreate);
    }

    public List<UserResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));
        return toResponse(user);
    }

    public User userUpdadte(Long id, UserRequest request) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return repository.save(user);
    }

    public void deleteById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));

        repository.deleteById(id);
    }

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());

        return response;
    }
}
