package eu.senla.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI openApiDescription() {
        Server localhostServer = new Server();
        localhostServer.setUrl("http://localhost:8080");
        localhostServer.setDescription("Local env");

        Contact contact = new Contact();
        contact.setEmail("igor.klimov.cc@gmail.com");

        License mitLicense = new License().name("GNU AGPLv3")
                .url("https://chooselicense.com/licenses/agpl-3.0");

        Info info = new Info()
                .title("BookInn API")
                .version("1.0")
                .contact(contact)
                .description("API for the Book Inn app")
                .license(mitLicense);

        return new OpenAPI()
                .info(info).servers(List.of(localhostServer));
    }
}
