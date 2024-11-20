package org.pentera.passwordcracker.minion.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pentera.passwordcracker.dto.CrackResultDTO;

public class CrackPasswordServiceTest {
    @Test
    public void testCrackPasswordFindsMatch() {
        MinionService service = new MinionService();
        String expectedPassword = "050-6880727";
        String hash = "93767ae313002380f8068a18aaa10d51";
        long start = 6880720;
        long end = 6880729;

        CrackResultDTO result = service.processTask(hash, start, end);
        assertEquals(hash, result.getHash());
        assertEquals(expectedPassword, result.getCrackedPassword());
        assertEquals(CrackResultDTO.Status.CRACKED, result.getStatus());
    }

    @Test
    public void testCrackPasswordNoMatch() {
        MinionService service = new MinionService();
        String hash = "somehash";
        long start = 26880720;
        long end = 26880729;

        CrackResultDTO result = service.processTask(hash, start, end);
        assertEquals(hash, result.getHash());
        assertNull(result.getCrackedPassword());
        assertEquals(CrackResultDTO.Status.NOT_IN_RANGE, result.getStatus());
    }

    @Test
    public void testCrackPasswordFailed() {
        MinionService service = Mockito.mock(MinionService.class);

        String hash = "93767ae313002380f8068a18aaa10d51";
        long start = 26880720;
        long end = 26880729;

        when(service.crackPassword(hash, start, end)).thenThrow(RuntimeException.class);
        CrackResultDTO expectedResult = new CrackResultDTO(hash, null, CrackResultDTO.Status.NOT_IN_RANGE);
        when(service.processTask(hash, start, end)).thenReturn(expectedResult);
        CrackResultDTO result = service.processTask(hash, start, end);
        assertEquals(hash, result.getHash());
        assertNull(result.getCrackedPassword());
        assertEquals(CrackResultDTO.Status.NOT_IN_RANGE, result.getStatus());
    }
}
