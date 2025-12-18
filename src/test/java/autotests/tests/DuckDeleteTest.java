package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.DeleteResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Delete")
public class DuckDeleteTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Создание, удаление и валидация утки только через БД")
    @CitrusTest
    public void testDeleteDuckDataBaseOnly(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Удаление утки (валидация через строку)")
    @CitrusTest
    public void testDeleteDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        deleteDuck(runner, duckId);

        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Удаление утки (валидация через ресурс)")
    @CitrusTest
    public void testDeleteDuckResourceValidation(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = duckClientDB.createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

        deleteDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckDeleteTest/deleteResponse.json");
    }

    @Test(description = "Удаление утки (валидация через payload-модель)")
    @CitrusTest
    public void testDeleteDuckPayloadValidation(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "red", 50, "rubber", "quack", "FIXED");

        deleteDuck(runner, duckId);

        DeleteResponse expectedResponse = new DeleteResponse("Duck is deleted");

        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);
    }
}