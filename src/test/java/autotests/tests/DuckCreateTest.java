package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Create")
public class DuckCreateTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Создать утку с material = rubber и проверить её наличие в БД")
    @CitrusTest
    public void testCreateRubberDuckInDatabase(@Optional @CitrusResource TestCaseRunner runner) {
        // 1 создаём утку в БД
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        // 2 проверяем, что утка действительно создалась в БД
        duckClientDB.validateDuckInDatabase(runner, duckId);

        // 3 проверяем все данные утки в БД
        duckClientDB.validateDuckInDatabase(runner, duckId, "yellow", "0.121", "rubber", "quack", "ACTIVE");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        // 5 проверяем, что утка действительно удалилась
        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Создать утку с material = wood и проверить её наличие в БД")
    @CitrusTest
    public void testCreateWoodDuckInDatabase(@Optional @CitrusResource TestCaseRunner runner) {
        // 1 создаём утку в БД
        String duckId = duckClientDB.createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

        // 2 проверяем, что утка действительно создалась в БД
        duckClientDB.validateDuckInDatabase(runner, duckId);

        // 3 проверяем все данные утки в БД
        duckClientDB.validateDuckInDatabase(runner, duckId, "brown", "0.2", "wood", "quack", "FIXED");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        // 5 проверяем, что утка действительно удалилась
        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }
}