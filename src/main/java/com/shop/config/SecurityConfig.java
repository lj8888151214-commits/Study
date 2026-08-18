package com.shop.config;


import com.shop.dto.MemberFormDto;
import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration // 설정 클래스
@EnableWebSecurity // 웹 보안 가능하게하는 클래스
public class SecurityConfig {
    @Autowired
    MemberService memberService;

    // 허용여부 설정하는 메소드
    // @Bean 객체 -> 스프링 컨테이너 관리하고 사용하는 싱글톤
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {



        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**","/img/**","/favicon.ico","/error").permitAll()
                        .requestMatchers("/","/members/**","/item/**","/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/members/login")
                        .defaultSuccessUrl("/")
                        .usernameParameter("email")
                        .failureUrl("/members/login/error")
                )
                .logout(logout -> logout
                        .logoutUrl("/members/logout") // import 필요
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
    // @Bean 객체 -> 스프링 컨테이너 관리하고 사용하는 싱글톤
    // 암호 -> 암호화 기능
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }
}
