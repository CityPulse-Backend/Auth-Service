package city.pulse.auth_service.feature.auth.register.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import city.pulse.auth_service.feature.auth.register.service.RegisterService;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static city.pulse.auth_service.feature.auth.common.data.UserTestData.*;

@WebMvcTest(RegisterController.class)
class RegisterControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private RegisterService registerService;

    @Test
    @WithMockUser
    void register_created() throws Exception {
        when(registerService.createUser(ALICE, ALICE_RAW, ALICE_EMAIL)).thenReturn(aliceDto());

        mvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
          {"username":"alice","password":"secret","email":"alice@example.com"}
        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/users/1")))
                .andExpect(jsonPath("$.username").value(ALICE));
    }
}
