package com.security.website;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Le profil "h2" est autonome (datasource H2 en memoire + JWT + CORS),
// ce qui permet au contexte de demarrer sans MySQL ni application.properties
// (ce dernier est gitignore, donc absent en CI).
@SpringBootTest
@ActiveProfiles("h2")
class WebsiteApplicationTests {

    @Test
    void contextLoads() {
    }

}
