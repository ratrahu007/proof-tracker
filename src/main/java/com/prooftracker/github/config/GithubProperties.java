package com.prooftracker.github.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "github")
public class GithubProperties {

    private String clientId;

    private String clientSecret;

    private String redirectUri;
}