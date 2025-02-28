import org.junit.jupiter.api.Test;
import org.restau.db.StaticDataSource;
import org.restau.entity.Dish;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DishTestStaticDataSource {
    @Test
    void getIngredientCost() {

        List<Dish> dishes = StaticDataSource.getDishes();
        Dish subject = dishes.get(0);
        Double expectedCost = 5500.0;

        Double actualCost = subject.getIngredientCost();

        assertEquals(expectedCost, actualCost, "Le prix total des ingredients doit retourner 5500");

    }

}