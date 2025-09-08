package co.com.crediya.auth.config;

import co.com.crediya.auth.model.security.gateways.LoginGateway;
import co.com.crediya.auth.model.security.gateways.PasswordService;
import co.com.crediya.auth.model.user.gateways.UserRepository;
import co.com.crediya.auth.usecase.login.LoginUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }


        @Bean
        public UserRepository UserRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public LoginGateway loginGateway() {
            return Mockito.mock(LoginGateway.class);
        }

        @Bean
        public PasswordService passwordService() {
            return Mockito.mock(PasswordService.class);
        }


    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}