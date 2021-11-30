package uk.gov.digital.ho.hocs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class Config {

    @Bean
    public HttpClient getHttpClient(){
        return HttpClient.newHttpClient();
    }

}
