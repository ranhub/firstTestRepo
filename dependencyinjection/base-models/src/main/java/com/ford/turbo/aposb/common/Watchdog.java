package com.ford.turbo.aposb.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
public class Watchdog implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(Watchdog.class);
    private static Watchdog watchdog;

    private Environment environment;

    public Watchdog() {
        watchdog = this;
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    private static Watchdog getInstance() {
        return watchdog;
    }

    boolean isCloudProfile() {
        final String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("cloud".equals(profile)) {
                return true;
            }
        }
        return false;
    }

    public static void startWatching() {
        if (getInstance().isCloudProfile()) {
            copyWatchdogScript();
            try {
                new ProcessBuilder("bash", "watchdog.sh")
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LOG.info("Watchdog disabled - only runs on the cloud profile");
        }
    }

    public static void copyWatchdogScript(){
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

        Resource resource = resourceLoader.getResource("watchdog.sh");
        File destFile = new File("watchdog.sh");

        InputStreamReader inputStreamReader = null;
        FileOutputStream fileOutputStream = null;

        try {

            inputStreamReader = new InputStreamReader(resource.getInputStream());
            fileOutputStream = new FileOutputStream(destFile);

            int i;
            while(( i = inputStreamReader.read() ) != -1){
                fileOutputStream.write(i);
            }

            inputStreamReader.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
