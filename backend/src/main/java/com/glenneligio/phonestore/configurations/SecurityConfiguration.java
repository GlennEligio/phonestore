package com.glenneligio.phonestore.configurations;

import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.filters.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfiguration(UserDetailsService userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf().disable();
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                // FOR ORDERS
                .requestMatchers(HttpMethod.GET, "/api/*/orders/*/items", "/api/*/users/@self/orders").hasAuthority(UserType.CUSTOMER.getType())
                .requestMatchers(HttpMethod.PUT, "/api/*/orders/*/items/*").hasAuthority(UserType.CUSTOMER.getType())
                .requestMatchers(HttpMethod.POST, "/api/*/orders/*/items", "/api/*/orders").hasAuthority(UserType.CUSTOMER.getType())
                .requestMatchers(HttpMethod.DELETE, "/api/*/orders/*/items/*", "/api/*/orders/*").hasAuthority(UserType.CUSTOMER.getType())
                // FOR GETTING BRAND AND PRODUCT INFO
                .requestMatchers(HttpMethod.GET, "/api/*/brands").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/*/phones").permitAll()
                // LOGIN AND REGISTER
                .requestMatchers(HttpMethod.POST, "/api/*/users/login", "/api/*/users/register").permitAll()
                .anyRequest().hasAnyAuthority(UserType.ADMIN.getType());
//                .anyRequest().permitAll();
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setMaxAge(Duration.ofMinutes(10));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
