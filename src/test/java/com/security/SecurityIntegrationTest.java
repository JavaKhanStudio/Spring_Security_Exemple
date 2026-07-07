package com.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration de bout en bout : demarre le contexte complet (profil h2,
 * base en memoire) et exerce les regles d'autorisation definies dans
 * {@code SecurityConfiguration} via MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicTestRoute_isAccessible_withoutAuthentication() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello world Test!"));
    }

    @Test
    void adminRoute_isForbidden_withoutAuthentication() throws Exception {
        mockMvc.perform(get("/admin/users/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminRoute_isForbidden_forNonAdminUser() throws Exception {
        mockMvc.perform(get("/admin/users/test"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminRoute_isAccessible_forAdminUser() throws Exception {
        mockMvc.perform(get("/admin/users/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is ok, you can get in"));
    }
}
