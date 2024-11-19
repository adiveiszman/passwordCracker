package org.pentera.passwordcracker.minion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.pentera.passwordcracker.minion.utils.Utils.passwordToLong;

import org.junit.jupiter.api.Test;
import org.pentera.passwordcracker.dto.TaskResultDTO;

public class CrackPasswordServiceTest {
    @Test
    public void testCrackPasswordFindsMatch() {
        MinionService service = new MinionService();
        String expectedPassword = "050-6880727";
        String hash = "93767ae313002380f8068a18aaa10d51";
        String start = "050-6880720";
        String end = "050-6880729";

        TaskResultDTO result = service.processRange(hash, start, end);
        assertEquals(hash, result.getHash());
        assertEquals(expectedPassword, result.getCrackedPassword());
        assertEquals(TaskResultDTO.Status.CRACKED, result.getStatus());
    }

    @Test
    public void testCrackPasswordNoMatch() {
        MinionService service = new MinionService();
        String hash = "somehash";
        String start = "052-6880720";
        String end = "052-6880729";

        TaskResultDTO result = service.processRange(hash, start, end);
        assertEquals(hash, result.getHash());
        assertNull(result.getCrackedPassword());
        assertEquals(TaskResultDTO.Status.NOT_FOUND, result.getStatus());
    }

    @Test
    public void testCrackPasswordFailed() {
        MinionService service = new MinionService();
        String hash = "93767ae313002380f8068a18aaa10d51";
        String start = "052-6880720";
        String end = "052-6880729";

        when(service.crackPassword(hash, passwordToLong(start), passwordToLong(end))).thenThrow(RuntimeException.class);

        TaskResultDTO result = service.processRange(hash, start, end);
        assertEquals(hash, result.getHash());
        assertNull(result.getCrackedPassword());
        assertEquals(TaskResultDTO.Status.NOT_FOUND, result.getStatus());
    }
}
