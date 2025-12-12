package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:config-${env}.properties",
        "classpath:config-dev.properties" // значение по умолчанию
})
public interface EnvironmentConfig extends Config {

    @Key("baseUrl")
    String baseUrl();

    @Key("env")
    @DefaultValue("dev")
    String environment();
}
