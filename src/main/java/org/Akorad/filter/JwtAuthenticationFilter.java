package org.Akorad.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.Akorad.service.security.OurUserDetailsService;
import org.Akorad.util.JWTUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    private final OurUserDetailsService userDetailsService;

    // Метод, выполняемый для каждого HTTP запроса
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Шаг 1: Извлечение заголовка авторизации из запроса

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Шаг 2: Проверка наличия заголовка авторизации
        if (authHeader==null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Шаг 3: Извлечение токена из заголовка
        jwt = authHeader.substring(7);

        // Шаг 4: Извлечение имени пользователя из JWT токена
        try {
            username = jwtUtils.extractUsername(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            e.printStackTrace();
            return;
        }

        // Шаг 5: Проверка валидности токена и аутентификации

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Шаг 6: Создание нового контекста безопасности

            if (jwtUtils.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }
        // Шаг 7: Передача запроса на дальнейшую обработку в фильтрующий цепочке

        filterChain.doFilter(request, response);
    }
}
