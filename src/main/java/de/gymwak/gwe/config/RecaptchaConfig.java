package de.gymwak.gwe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RecaptchaConfig {
    private static final String API_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    @Bean
    public static RecaptchaValidator recaptchaValidator(@Value("${RECAPTCHA_SECRET}") String secretKey) {
        return new RecaptchaValidator(secretKey);
    }

    public static class RecaptchaValidator {
        private final RestTemplate restTemplate;
        private final String secretKey;

        private RecaptchaValidator(String secretKey) {
            restTemplate = new RestTemplate();
            this.secretKey = secretKey;
        }

        public boolean validate(String userResponse) {
            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
            parameters.add("secret", secretKey);
            parameters.add("response", userResponse);

            RecaptchaResponse res = restTemplate.postForObject(API_ENDPOINT, parameters, RecaptchaResponse.class);
            return res.success;
        }
    }

    private static class RecaptchaResponse {
        public boolean success;
    }
}
