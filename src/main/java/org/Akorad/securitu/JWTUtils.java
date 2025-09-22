package org.Akorad.securitu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@RequiredArgsConstructor
public class JWTUtils {
    private final SecretKey secretKey;

    private static final Long EXPIRATION_TIME = 24*60*60*1000L; // 1 day in milliseconds

    private static final Long REFRESH_EXPIRATION_TIME = 7*24*60*60*1000L; // 7 days in milliseconds
}
