package com.ran.spring.example.dependencyinjection.controllers;

import com.ran.spring.example.dependencyinjection.services.HelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingsController {

  private HelloWorldService helloWorldService;

  @Autowired
  public void setHelloWorldService(HelloWorldService helloWorldService) {
    this.helloWorldService = helloWorldService;
  }

  public String sayGreeting() {
    System.out.println( helloWorldService.sayHello());
    return   helloWorldService.sayHello();
  }

}
