package org.Akorad.service.security;

import lombok.RequiredArgsConstructor;
import org.Akorad.repository.UserRepository;
import org.Akorad.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedLoginService {
    private final UserService userService;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    public void onFailedLogin(String username) {
        var user = userService.getUserByUsername(username);
        if (user != null) {
            int failedAttempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(failedAttempts);
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                user.setIsAccountNonLocked(false);
            }
            userService.updateUser(user.getId(), user);
        }
    }

    public void resetFailedAttempts(String username) {
        var user = userService.getUserByUsername(username);
        if (user != null && user.getFailedAttempt() > 0) {
            user.setFailedAttempt(0);
            userService.updateUser(user.getId(), user);
        }
    }

    public void unlockUser(String username) {
        var user = userService.getUserByUsername(username);
        if (user != null && !user.getIsAccountNonLocked()) {
            user.setIsAccountNonLocked(true);
            user.setFailedAttempt(0);
            userService.updateUser(user.getId(), user);
        }
    }
}
