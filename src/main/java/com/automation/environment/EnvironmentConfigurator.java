package com.automation.environment;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EnvironmentConfigurator {

    protected static final Logger LOGGER = LogManager.getLogger(EnvironmentConfigurator.class);
    private static volatile EnvironmentConfigurator environmentConfigurator;
    private static Properties properties = new Properties();

    private EnvironmentConfigurator() throws IOException {

    /** parse config.groovy and set one environment, which name is taken from system properties
     * It will be used to get it's inside url and parameters
     * In any case url and other params can be directly overridden if specified in system properties or gradle */
        ConfigSlurper configSlurper = new ConfigSlurper();
        configSlurper.setEnvironment(getTestEnvironment());
        ConfigObject configObject = configSlurper.parse(new File("config.groovy").toURI().toURL());

        configObject.flatten();
        properties = configObject.toProperties();
    }

    public static EnvironmentConfigurator getInstance() {
        EnvironmentConfigurator sysProps = environmentConfigurator;
        if (sysProps == null) {
            synchronized (EnvironmentConfigurator.class) {
                sysProps = environmentConfigurator;
                if (sysProps == null) {
                    try {
                        environmentConfigurator = sysProps = new EnvironmentConfigurator();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
        return sysProps;
    }

    public static String getTestEnvironment() {
        return System.getProperty("env", "dflt");
    }

    public String getAppUrl() {
        return System.getProperty("url", properties.get("url").toString());
    }

    public String getBrowserClient() {
        return System.getProperty("browserClient", "gc");
    }

    public Boolean isGridUsed() {
        return Boolean.parseBoolean(properties.getProperty("grid.isUsed"));
    }

    public String getSeleniumHub() {
        return properties.getProperty("grid.seleniumHub");
    }


    public String getAdminLogin() {
        return properties.getProperty("users.admin.login");
    }

    public String getPassword() {
        return properties.getProperty("users.password");
    }

    public String getApiBearerToken() {
        return properties.getProperty("api.bearerToken");
    }
}
