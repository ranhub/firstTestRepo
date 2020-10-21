package com.ran.spring.example.dependencyinjection.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"englsh","default"})
public class HelloWorldServiceEnglishImpl implements HelloWorldService {


    @Override
    public String sayHello() {
        return "Hello World !!!";
    }
}
