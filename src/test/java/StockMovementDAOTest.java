import org.junit.jupiter.api.Test;
import org.restau.dao.StockMovementDAO;
import org.restau.entity.Movement;
import org.restau.entity.StockMovement;
import org.restau.entity.Unit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StockMovementDAOTest {

    StockMovementDAO subject = new StockMovementDAO();
    @Test
    void testCreateAndReadStockMovement() {

        StockMovement stockMovement = new StockMovement(null, 5L, Movement.IN, new BigDecimal (500), Unit.G, LocalDateTime.now());
        stockMovement = subject.save(stockMovement);
        assertNotNull(stockMovement.getIdStockMovement(), "L'ID du mouvement de stock ne devrait pas être null après la création");

        Optional<StockMovement> fetchedStockMovement = subject.findById(stockMovement.getIdStockMovement());
        assertNotNull(fetchedStockMovement, "Le mouvement de stock doit être récupérable");
        assertEquals(5L, fetchedStockMovement.get().getIdIngredient());
        assertEquals(Movement.IN, fetchedStockMovement.get().getMovement());
        assertEquals(new BigDecimal("500.000"), fetchedStockMovement.get().getQuantity());
    }

}
