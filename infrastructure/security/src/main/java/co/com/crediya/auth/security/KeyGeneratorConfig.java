package co.com.crediya.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class KeyGeneratorConfig {

    @Bean
    public KeyPair jwtKeyPair() {
        return generateRsaKey();
    }

    @Bean
    public RSAPrivateKey jwtPrivateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    @Bean
    public RSAPublicKey jwtPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    private KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate RSA key pair", e);
        }
    }
}