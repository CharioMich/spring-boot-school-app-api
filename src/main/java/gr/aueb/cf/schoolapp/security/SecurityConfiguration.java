package gr.aueb.cf.schoolapp.security;

import gr.aueb.cf.schoolapp.authentication.JwtAuthenticationFilter;
import gr.aueb.cf.schoolapp.core.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * This class configures authentication and authorization
 * @Bean -> That means the object returned by each annotated method is stored in the application context and managed by Spring.
 * Later, Spring Security can automatically use it when authenticating users.
 */
@Configuration  // Enables @Bean on class methods
@EnableWebSecurity  // Enables Spring Security’s filter chain.
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configures filter ordering via a Spring Boot DSL (domain specific language)
     * It's a sequence of Servlet filters automatically registered by Spring Security.
     * Each filter has a very specific job (e.g., authentication, authorization, exception handling, CORS, CSRF).
     * Requests flow through the filters in order until they reach your application (or get rejected).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(myCustomAuthenticationEntryPoint()))
                .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(myCustomAccessDeniedHandler()))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/teachers/save", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/authenticate").permitAll()
                        .requestMatchers("/api/teachers/**").hasAnyAuthority(Role.TEACHER.name(), Role.SUPER_ADMIN.name())
                        .requestMatchers("/api/employees/**").hasAnyAuthority(Role.EMPLOYEE.name())
                        .requestMatchers("/**").permitAll() // for Swagger
                )
                .sessionManagement((session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))) // REST api - JWT Authentication
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("https://coding-factory.apps.gov.gr",
                "https://test-coding-factory.apps.gov.gr", "http://localhost:4200", "http://localhost:5173"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


    /**
     * Validates an Authentication object (like a login attempt).
     * It either returns a fully authenticated Authentication (with user details + roles), or
     * throws an AuthenticationException (bad credentials, user not found, etc.)
     */
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Encodes password using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }


    /**
     * Gives access to the central Spring Boot's AuthenticationManage
     * To be used in AuthenticationService
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint myCustomAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler myCustomAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}
