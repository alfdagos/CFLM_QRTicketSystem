package it.cflm.qrticketsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurazione Spring Security per autenticazione e autorizzazione.
 * 
 * Ruoli:
 * - ADMIN: accesso completo (gestione, reception, API)
 * - RECEPTION: accesso solo alla verifica biglietti
 * - USER: accesso solo alla creazione biglietti
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configurazione della catena di filtri di sicurezza.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Risorse pubbliche (CSS, JS, immagini)
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()
                
                // Swagger UI pubblico
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                
                // Console H2 (solo per sviluppo)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Homepage pubblica per creare biglietti
                .requestMatchers("/", "/index").permitAll()
                .requestMatchers("/tickets", "/api/tickets").permitAll()
                
                // Visualizzazione biglietto e QR code pubblici
                .requestMatchers("/ticket/**", "/qrcode/**").permitAll()
                
                // Reception richiede ruolo RECEPTION o ADMIN
                .requestMatchers("/reception/**").hasAnyRole("RECEPTION", "ADMIN")
                
                // Qualsiasi altra richiesta richiede autenticazione
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/reception", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                // Disabilita CSRF per H2 console
                .ignoringRequestMatchers("/h2-console/**")
                // Disabilita CSRF per API REST (in produzione usare token)
                .ignoringRequestMatchers("/api/**")
            )
            .headers(headers -> headers
                // Permetti frames per H2 console
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    /**
     * Definizione degli utenti in memoria (per semplicità).
     * In produzione, utilizzare un database con UserDetailsService personalizzato.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails reception = User.builder()
                .username("reception")
                .password(passwordEncoder().encode("reception123"))
                .roles("RECEPTION")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, reception, user);
    }

    /**
     * Password encoder per criptare le password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
