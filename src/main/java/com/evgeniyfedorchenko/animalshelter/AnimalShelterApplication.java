package com.evgeniyfedorchenko.animalshelter;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Animal Shelter API",
                description = "The API for the administration of the 'Animal Shelter' service: management, receipt and modification of data and entities",
                version = "1.0.0",
                contact = @Contact(
                        name = "Fedorchenko Evgeniy",
                        url = "https://github.com/evgeniy-fedorchenko",
                        email = "jecky432@gmail.com"
                )
        )
)
@SpringBootApplication
public class AnimalShelterApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalShelterApplication.class, args);
	}

}
