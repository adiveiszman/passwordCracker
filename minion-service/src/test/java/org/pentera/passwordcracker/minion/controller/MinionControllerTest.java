package org.pentera.passwordcracker.minion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.pentera.passwordcracker.dto.CrackResultDTO;
import org.pentera.passwordcracker.minion.service.PasswordCrackMinionService;
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
    private PasswordCrackMinionService crackPasswordService;

    @Test
    public void testCrackEndpoint() throws Exception {
        String hash = "hash";
        long start = 0;
        long end = 0;
        CrackResultDTO result = new CrackResultDTO(hash, "050-0000000", CrackResultDTO.Status.CRACKED);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> payload = new HashMap<>();
        payload.put("hash", hash);
        payload.put("startRange", start);
        payload.put("endRange", end);

        String jsonContent = objectMapper.writeValueAsString(payload);
        String expectedResponse = objectMapper.writeValueAsString(result);

        when(crackPasswordService.processTask(hash, start, end)).thenReturn(result);

        mockMvc.perform(post("/minion/crack")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
