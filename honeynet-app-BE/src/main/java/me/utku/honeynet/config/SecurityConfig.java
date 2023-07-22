package me.utku.honeynet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.security.CustomAuthenticationEntryPoint;
import me.utku.honeynet.dto.security.JsonAuthFailureHandler;
import me.utku.honeynet.dto.security.JsonAuthSuccessHandler;
import me.utku.honeynet.dto.security.JsonLogoutSuccessHandler;
import me.utku.honeynet.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserService userService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public JsonAuthSuccessHandler jsonAuthSuccessHandler() {
        return new JsonAuthSuccessHandler(objectMapper);
    }

    @Bean
    public JsonAuthFailureHandler jsonAuthFailureHandler(){
        return new JsonAuthFailureHandler(objectMapper);
    }

    @Bean
    public JsonLogoutSuccessHandler jsonLogoutSuccessHandler(){
        return new JsonLogoutSuccessHandler(objectMapper);
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(req -> req
                .requestMatchers("/pot/**").hasAuthority("ADMIN")
                .requestMatchers("/user/**").hasAuthority("ADMIN")
                .requestMatchers("/suspicious/client/**").hasAuthority("ADMIN")
                .requestMatchers("/**").permitAll()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint()))
            .formLogin(form -> form
                .successHandler(jsonAuthSuccessHandler())
                .failureHandler(jsonAuthFailureHandler())
            )
            .logout(logout -> logout
                .deleteCookies("JSESSIONID","authenticated")
                .invalidateHttpSession(true)
                .logoutSuccessHandler(jsonLogoutSuccessHandler())
            )
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        /*
        * Request would be rejected if the allowed origins is set to * and allowCredentials is set to true.
        When enabling allowCredentials, providing url(s) in allowedOrigins is a must for security reasons.
        * Finally, JSESSION would not be set in the browser cookies if the allowed origins is set to * and allowCredentials is not set.
        */
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("Accept", "Authorization", "Content-Type", "In-App-Auth-Token","Origin", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}