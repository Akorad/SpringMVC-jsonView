package org.Akorad.dto;

import lombok.AllArgsConstructor;

public record AuthResponse(String accessToken, String refreshToken) {
}
