package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.DeleteResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Delete")
public class DuckDeleteTest extends DuckClient {

    // новый тест для 1 части 30 домашки
    @Test(description = "Создание, удаление и валидация утки только через БД")
    @CitrusTest
    public void testDeleteDuckDataBaseOnly(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner,"yellow", 0.121, "rubber", "quack", "ACTIVE");

        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);

        validateDuckNotExistsInDatabase(runner, duckId);

    }

    @Test(description = "Удаление утки (валидация через строку)")
    @CitrusTest
    public void testDeleteDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {

        // создание через БД, удаление старым способом, иначе название теста не будет соответствовать самому тесту
        String duckId = DuckClientDB.createDuckInDatabase(this, runner,"yellow", 0.121, "rubber", "quack", "ACTIVE");

        deleteDuck(this, runner, duckId);

        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Удаление утки (валидация через ресурс)")
    @CitrusTest
    public void testDeleteDuckResourceValidation(@Optional @CitrusResource TestCaseRunner runner) {

        // создание через БД, удаление старым способом, иначе название теста не будет соответствовать самому тесту
        String duckId = DuckClientDB.createDuckInDatabase(this, runner,"brown", 0.2, "wood", "quack", "FIXED");

        deleteDuck(this, runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckDeleteTest/deleteResponse.json");
    }

    @Test(description = "Удаление утки (валидация через payload-модель)")
    @CitrusTest
    public void testDeleteDuckPayloadValidation(@Optional @CitrusResource TestCaseRunner runner) {

        // создание через БД, удаление старым способом, иначе название теста не будет соответствовать самому тесту
        String duckId = DuckClientDB.createDuckInDatabase(this, runner,"red", 50, "rubber", "quack", "FIXED");

        deleteDuck(this, runner, duckId);

        // через payload-модель
        DeleteResponse expectedResponse = new DeleteResponse("Duck is deleted");
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);
    }
}