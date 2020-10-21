package com.ran.spring.example.dependencyinjection.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("spanish")
public class HelloWorldServiceSpanishImpl implements HelloWorldService {

   @Override
    public String sayHello() {
        return "Hola Mundoo !!!";
    }
}
