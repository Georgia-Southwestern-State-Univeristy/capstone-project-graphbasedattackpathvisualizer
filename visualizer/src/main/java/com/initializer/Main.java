/* 
 Entry point for the Graph-Based Attack Path Visualizer application.
 This Spring Boot application initializes the backend services and
 exposes REST APIs for retrieving the attack graph structure and
 supporting future attack path computation.
*/
package com.initializer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}