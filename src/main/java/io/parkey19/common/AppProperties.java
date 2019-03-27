package io.parkey19.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

/**
 * Created by parkey19 on 2019. 3. 27..
 */
@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {

    @NotEmpty
    private String adminUsername;
    @NotEmpty
    private String adminPassword;
    @NotEmpty
    private String userUsername;
    @NotEmpty
    private String userPassword;
    @NotEmpty
    private String clientId;
    @NotEmpty
    private String clientSecret;

}
