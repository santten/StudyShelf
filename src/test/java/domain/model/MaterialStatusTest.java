package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaterialStatusTest {

    @Test
    void values() {
        MaterialStatus[] statuses = MaterialStatus.values();
        assertEquals(3, statuses.length);
        assertEquals(MaterialStatus.APPROVED, statuses[0]);
        assertEquals(MaterialStatus.PENDING, statuses[1]);
        assertEquals(MaterialStatus.REJECTED, statuses[2]);
    }

    @Test
    void valueOf() {
        assertTrue(MaterialStatus.valueOf("APPROVED") == MaterialStatus.APPROVED);
        assertTrue(MaterialStatus.valueOf("PENDING") == MaterialStatus.PENDING);
        assertTrue(MaterialStatus.valueOf("REJECTED") == MaterialStatus.REJECTED);
    }
}