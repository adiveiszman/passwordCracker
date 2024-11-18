package org.pentera.passwordcracker.minion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.pentera.passwordcracker.minion.service.CrackPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;

@WebMvcTest(MinionController.class)
public class MinionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrackPasswordService crackPasswordService;

    @Test
    public void testCrackEndpoint() throws Exception {
        String hash = "hash";
        String start = "050-0000000";
        String end = "050-0000000";
        String expectedPassword = "050-0000000";

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("hash", hash);
        payload.put("startRange", start);
        payload.put("endRange", end);

        String jsonContent = objectMapper.writeValueAsString(payload);

        when(crackPasswordService.crackPassword(hash, start, end)).thenReturn(expectedPassword);

        mockMvc.perform(post("/crack")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedPassword));
    }
}
