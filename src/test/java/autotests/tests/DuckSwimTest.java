package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.SwimResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

// TODO: SHIFT-AQA-3

@Epic("Duck Controller")
@Feature("Swim")
public class DuckSwimTest extends DuckClient {

    @Test(description = "Существующий id (валидация через ресурс)")
    @CitrusTest
    public void testSwimExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        swimDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.NOT_FOUND, "DuckSwimTest/swimNotFoundResponse.json");

        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Несуществующий id (валидация через payload-модель)")
    @CitrusTest
    public void testSwimNonExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        // создаём и сразу удаляем утку, чтоб она была несуществующей
        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");
        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);


        swimDuck(runner, duckId);
        // Валидация через payload-модель
        SwimResponse expectedResponse = new SwimResponse("Paws are not found ((((");
        validateResponseFromPayload(runner, HttpStatus.NOT_FOUND, expectedResponse);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Существующий id (валидация через строку)")
    @CitrusTest
    public void testSwimExistingDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        swimDuck(runner, duckId);

        // валидация через строку
        validateResponseFromString(runner, HttpStatus.NOT_FOUND, "{\"message\":\"Paws are not found ((((\"}");

        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }
}