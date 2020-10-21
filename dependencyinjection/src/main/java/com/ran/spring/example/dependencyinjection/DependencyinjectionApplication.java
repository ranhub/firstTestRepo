package com.ran.spring.example.dependencyinjection;

import com.ran.spring.example.dependencyinjection.controllers.GreetingsController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DependencyinjectionApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DependencyinjectionApplication.class, args);
        GreetingsController greetingsController = (GreetingsController) context.getBean("greetingsController");
        greetingsController.sayGreeting();
    }

}
