package org.Akorad.service;

import org.Akorad.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    User getUserByUsername(String username);
    List<User> getAllUsers();

    User getCurrentUser();

    Optional<User> getUserByEmail(String email);
}
