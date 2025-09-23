package org.Akorad.service;

import org.Akorad.entity.User;

import java.util.List;

public interface UserService {
    public User createUser(User user);
    public User getUserById(Long id);
    public User updateUser(Long id, User userDetails);
    public void deleteUser(Long id);
    public User getUserByUsername(String username);
    public List<User> getAllUsers();
}
