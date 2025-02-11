package infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void getEntityManagerFactory() {
        EntityManagerFactory emf1 = DatabaseConnection.getEntityManagerFactory();
        EntityManagerFactory emf2 = DatabaseConnection.getEntityManagerFactory();

        assertNotNull(emf1);
        assertSame(emf1, emf2);
        assertTrue(emf1.isOpen());
    }
}