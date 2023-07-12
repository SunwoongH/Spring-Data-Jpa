package study.springdatajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@Configuration
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        // 실제 사용 시 세션 정보를 가져온 후 유저 아이디를 꺼내서 넣으면 된다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
