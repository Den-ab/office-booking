package ru.otus.pw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.otus.pw.filters.JwtAuthenticationFilter;
import ru.otus.pw.services.AppUserDetailsService;

import java.util.List;

/**
 * Конфигурирование SS.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    //region Fields

    /**
     * Сервис для работы с пользователями в SS.
     */
    private final AppUserDetailsService appUserDetailsService;

    /**
     * Фильтр аутентификации пользователя по JWT токену.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //endregion
    //region Public

    /**
     * Бин шифрования для пароля.
     *
     * @return Сервис для шифрования пароля.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder(12);
    }

    /**
     * Бин провайдера аутентификатора.
     *
     * @return Провайдер аутентификатора.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(appUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    /**
     * Бин менеджера аутентификауией.
     *
     * @param config Конфигурация аутентификации.
     *
     * @return Менеджер аутентификауией.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /**
     * Бин цепочки фильтров.
     *
     * @param http Данные HTTP запроса.
     *
     * @return Цепочка фильтров.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/areas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/areas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/areas/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/desks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/desks").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/desks/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/bookings/*").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Бин конфигурации CORS.
     *
     * @return конфигурация CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    //endregion
}
