package org.scoula.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * 환경별 프로파일 설정 클래스
 */
@Configuration
public class ProfileConfig {
    
    @Configuration
    @Profile("dev")
    @PropertySource("classpath:config/enviroments/application-dev.properties")
    public static class DevConfig {
        // 개발환경 전용 Bean 설정
    }
    
    @Configuration
    @Profile("prod")
    @PropertySource("classpath:config/enviroments/application-prod.properties")
    public static class ProdConfig {
        // 운영환경 전용 Bean 설정
    }

    @Configuration
    @Profile("test")
    @PropertySource("classpath:config/enviroments/application-test.properties")
    public static class TestConfig {
        // 테스트
    }

    @Configuration
    @Profile("test22")
    @PropertySource("classpath:config/enviroments/application-test22.properties")
    public static class Test22Config {
        // 테스트22
    }
}
