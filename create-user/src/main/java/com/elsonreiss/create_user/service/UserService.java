package com.elsonreiss.create_user.service;

import com.elsonreiss.create_user.dto.request.UserRequest;
import com.elsonreiss.create_user.dto.response.UserResponse;
import com.elsonreiss.create_user.model.User;
import com.elsonreiss.create_user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        User userCreated = new User();
        userCreated.setName(user.getName());
        userCreated.setEmail(user.getEmail());
        userCreated.setPassword(user.getPassword());
        return userRepository.save(userCreated);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Id não encontrado"));
                return toResponse(user);
    }

    public User userUpdate(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return userRepository.save(user);
    }

    public void userDelete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Usuario não encontrado"); // ver a diferença de EntityNotFoundException e RuntimeException
        }
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }
}


