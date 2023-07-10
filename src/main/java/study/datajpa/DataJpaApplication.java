package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        /*
        보통은 이렇게 해놓으면 Security에서 contextHolder > context > authentication > pricipal에서 사용자를 꺼내거나,
        session 에서 사용자를 꺼내 여기에서 보내준다.
        */

        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
