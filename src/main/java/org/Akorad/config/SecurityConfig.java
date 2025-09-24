package org.Akorad.config;

import lombok.RequiredArgsConstructor;
import org.Akorad.filter.JwtAuthenticationFilter;
import org.Akorad.service.security.OurUserDetailsService;
import org.Akorad.filter.LoggingFilter;
import org.Akorad.service.security.SocialAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OurUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final LoggingFilter loggingFilter;
    private final SocialAppService socialAppService;
    private final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll() // Public endpoints
                    .anyRequest().authenticated() // All other endpoints require authentication
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Обработка ошибок аутентификации
                )
                .oauth2Login(oauth2 -> oauth2
                    .loginPage("/api/auth/oauth2/login") // Custom login page URL
                        .userInfoEndpoint(userInfo -> userInfo
                            .userService(socialAppService) // Custom user service to fetch user details
                        )
                    .defaultSuccessUrl("/api/users/user") // Redirect URL after successful login
                    .permitAll()
                )
                .logout(logout -> logout

                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler(oidcLogoutSuccessHandler())
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                );

        http.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            logger.info("User logged out: " + (authentication != null ? authentication.getName() : "anonymous"));
            response.sendRedirect("/");
        };
    }
}
