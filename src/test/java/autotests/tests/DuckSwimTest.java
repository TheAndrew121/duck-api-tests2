package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.SwimResponse;
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
@Feature("Swim")
public class DuckSwimTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Существующий id (валидация через ресурс)")
    @CitrusTest
    public void testSwimExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        swimDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.NOT_FOUND, "DuckSwimTest/swimNotFoundResponse.json");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Несуществующий id (валидация через payload-модель)")
    @CitrusTest
    public void testSwimNonExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        swimDuck(runner, duckId);

        SwimResponse expectedResponse = new SwimResponse("Paws are not found ((((");

        validateResponseFromPayload(runner, HttpStatus.NOT_FOUND, expectedResponse);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Существующий id (валидация через строку)")
    @CitrusTest
    public void testSwimExistingDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        swimDuck(runner, duckId);

        validateResponseFromString(runner, HttpStatus.NOT_FOUND, "{\"message\":\"Paws are not found ((((\"}");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }
}