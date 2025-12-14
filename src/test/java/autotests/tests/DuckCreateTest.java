package autotests.tests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Create")
public class DuckCreateTest extends DuckClient {

    @Test(description = "Создать утку с material = rubber и проверить её наличие в БД")
    @CitrusTest
    public void testCreateRubberDuckInDatabase(@Optional @CitrusResource TestCaseRunner runner) {
        // 1 создаём утку в БД
        String duckId = createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        // 2 проверяем, что утка действительно создалась в БД
        validateDuckInDatabase(runner, duckId);

        // 3 проверяем все данные утки в БД
        validateDuckInDatabase(runner, duckId, "yellow", "0.121", "rubber", "quack", "ACTIVE");

        // 4 удаляем утку через БД
        deleteDuckFromDatabase(runner, duckId);

        // 5 проверяем, что утка действительно удалилась
         validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Создать утку с material = wood и проверить её наличие в БД")
    @CitrusTest
    public void testCreateWoodDuckInDatabase(@Optional @CitrusResource TestCaseRunner runner) {
        // 1 создаём утку в БД
        String duckId = createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

        // 2 проверяем, что утка действительно создалась в БД
        validateDuckInDatabase(runner, duckId);

        // 3 проверяем все данные утки в БД
        validateDuckInDatabase(runner, duckId, "brown", "0.2", "wood", "quack", "FIXED");

        // 4 удаляем утку через БД
        deleteDuckFromDatabase(runner, duckId);

        // 5 проверяем, что утка действительно удалилась
        validateDuckNotExistsInDatabase(runner, duckId);
    }
}