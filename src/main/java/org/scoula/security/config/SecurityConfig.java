package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.security.filter.AuthenticationErrorFilter;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.CustomAccessDeniedHandler;
import org.scoula.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;


/**
 * Spring Security 메인 설정 클래스
 *
 * 주요 기능:
 * - 데이터베이스 기반 사용자 인증
 * - 커스텀 로그인/로그아웃 페이지
 * - 경로별 접근 권한 제어
 * - 한글 문자 인코딩 처리
 * - BCrypt 비밀번호 암호화
 */
@Configuration
@EnableWebSecurity  // Spring Security 활성화
@Slf4j
@MapperScan(basePackages = {"org.scoula.security.account.mapper"})  // 매퍼 스캔 설정
@ComponentScan(basePackages = {"org.scoula.security"})    // 서비스 클래스 스캔
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserDetailsService userDetailsService;   // CustomUserDetailsService 주입

    // JWT 인증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 인증 예외 처리 필터
    private final AuthenticationErrorFilter authenticationErrorFilter;

    // 401/403 에러 처리 핸들러
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    // 커스텀 인증 필터 추가
    @Autowired
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;


    /**
     * 비밀번호 암호화기 Bean 등록
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt 해시 함수 사용
    }


    /**
     * 한글 문자 인코딩 필터 생성
     * @return CharacterEncodingFilter 인스턴스
     */
//    public CharacterEncodingFilter encodingFilter() {
//        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
//        encodingFilter.setEncoding("UTF-8");           // UTF-8 인코딩 설정
//        encodingFilter.setForceEncoding(true);         // 강제 인코딩 적용
//        return encodingFilter;
//    }


    // AuthenticationManager 빈 등록 - JWT 토큰 인증에서 필요
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    /**
     * HTTP 보안 설정 메서드 (웹 애플리케이션의 보안 정책을 상세하게 구성)
     * @param http HttpSecurity 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                 //.addFilterBefore(encodingFilter(), CsrfFilter.class)// 한글 인코딩 필터 설정
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class) // 인증 에러 필터
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Jwt 인증필터
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // API 로그인 인증 필터

                // 예외 처리 설정
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 401 에러 처리
                .accessDeniedHandler(accessDeniedHandler);           // 403 에러 처리


        //  HTTP 보안 설정
        http.httpBasic().disable()      // 기본 HTTP 인증 비활성화
                .csrf().disable()           // CSRF 보호 비활성화 (REST API에서는 불필요)
                .formLogin().disable()      // 폼 로그인 비활성화 (JSON 기반 API 사용)
                .sessionManagement()        // 세션 관리 설정
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // 무상태 모드


        // 인증 요구 경로 설정
        http
                .authorizeRequests() // 경로별 접근 권한 설정
                .antMatchers(HttpMethod.OPTIONS).permitAll()

                //.anyRequest().authenticated(); // 현재는 모든 접근 허용 (개발 단계)

                // 회원 관련 인증 요구 경로
//                .antMatchers(HttpMethod.POST, "/api/member").authenticated() // 회원 등록
//                .antMatchers(HttpMethod.PUT, "/api/member", "/api/member/*/changepassword").authenticated() // 회원 정보 수정, 비밀번호 변경

                // 🌐 회원 관련 공개 API (인증 불필요)
                .antMatchers(HttpMethod.GET, "/api/member/checkusername/**").permitAll()     // ID 중복 체크
                .antMatchers(HttpMethod.POST, "/api/member").permitAll()                    // 회원가입
                .antMatchers(HttpMethod.GET, "/api/member/*/avatar").permitAll()            // 아바타 이미지

                // 🔒 회원 관련 인증 필요 API
                .antMatchers(HttpMethod.PUT, "/api/member/**").authenticated() // 회원 정보 수정, 비밀번호 변경

                // 게시판 관련 인증 요구 경로
                .antMatchers(HttpMethod.POST, "/api/board/**").authenticated() // 쓰기
                .antMatchers(HttpMethod.PUT, "/api/board/**").authenticated()  // 수정
                .antMatchers(HttpMethod.DELETE, "/api/board/**").authenticated() // 삭제

                .anyRequest().permitAll(); // 나머지 허용
    }


    /**
     * 인증 관리자 설정 메서드
     * 사용자 인증 방식과 비밀번호 암호화 방식을 설정
     * @param auth AuthenticationManagerBuilder 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    // Spring Security에서 인증 방식과 사용자 정보를 어떻게 처리할지 정의
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("configure .........................................");

        // UserDetailsService와 PasswordEncoder 설정
        auth.userDetailsService(userDetailsService)         // 커스텀 서비스 사용
                .passwordEncoder(passwordEncoder());        // BCrypt 암호화 사용

    }


    // 브라우저의 CORS 정책을 우회하여 다른 도메인에서의 API 접근 허용
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);           // 인증 정보 포함 허용
        config.addAllowedOriginPattern("*");        // 모든 도메인 허용
        config.addAllowedHeader("*");               // 모든 헤더 허용
        config.addAllowedMethod("*");               // 모든 HTTP 메서드 허용

        source.registerCorsConfiguration("/**", config);  // 모든 경로에 적용
        return new CorsFilter(source);
    }


    // Spring Security 검사를 우회할 경로 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/assets/**",      // 정적 리소스
                "/*",              // 루트 경로의 파일들
                // "/api/member/**",   // 회원 관련 공개 API <- 삭제

                // Swagger 관련 URL은 보안에서 제외
                "/swagger-ui.html", "/webjars/**",
                "/swagger-resources/**", "/v2/api-docs"
        );
    }
}
