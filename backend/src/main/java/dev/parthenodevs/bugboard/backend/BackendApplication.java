package dev.parthenodevs.bugboard.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication
{
    private BackendApplication(){}

    static void main(String[] args)
    {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println(
                "ciao"
        );
    }
}
