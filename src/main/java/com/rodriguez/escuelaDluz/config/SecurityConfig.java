package com.rodriguez.escuelaDluz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso a todos a "/" y "/studentAppointmentsView"
                .requestMatchers("/", "/studentAppointmentsView", "/css/**", "/js/**", "/img/**", "/studentAppointmentsView", "/error").permitAll()
                
                // Acceso exclusivo para ADMIN
                .requestMatchers("/employee/**", "/variables/**", "/users/new" , "users/edit/*", "/users/delte/*", "/users/save").hasRole("ADMIN")
                
                // Acceso para ADMIN y RECEPCIONISTA
                .requestMatchers("/imagenes/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                
                // Páginas accesibles por ADMIN y RECEPCIONISTA
                .requestMatchers(
                    "/login", "/appointments/view**", "/payments/view**",
                    "/employees/list", "/home/graduate", "/home", "/filter**", "/payments/view/filter**",
                    "/filter/graduate", "/appointments/view/filter**", "/appointment/**",
                    "/appointment/edit/*/*", "/appointment/Complete/*", "/appointment/Complete/**",
                    "/appointment/Complete/view/*", "/appointment/Complete/view/*/2",
                    "/appointment/Complete/student/**", "/save/appointment",
                    "/ignore/appointment/*", "/employee/view/*", "/exam/**", "/student",
                    "/student/**", "/horarios-disponibles", "/horarios-consecutivos-disponibles",
                    "/payment/**", "/inactivate/*", "/ignore/*", "/homeFilter**", "/home/graduate**", "/users", "/users/recepcionistNew"
                    , "/users/save-recepcionist"
                ).hasAnyRole("ADMIN", "RECEPCIONISTA")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
