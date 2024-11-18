package org.pentera.passwordcracker.minion.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class CrackPasswordServiceTest {
    @Test
    public void testCrackPasswordFindsMatch() {
        CrackPasswordService service = new CrackPasswordService();
        String expected = "050-6880727";
        String hash = "93767ae313002380f8068a18aaa10d51";
        String start = "050-6880720";
        String end = "050-6880729";

        String result = service.crackPassword(hash, start, end);
        assertEquals(expected, result);
    }

    @Test
    public void testCrackPasswordNoMatch() {
        CrackPasswordService service = new CrackPasswordService();
        String hash = "somehash";
        String start = "052-6880720";
        String end = "052-6880729";

        String result = service.crackPassword(hash, start, end);
        assertNull(result);
    }
}
