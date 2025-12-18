package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.FlyResponse;
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
@Feature("Fly")
public class DuckFlyTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Существующий id с ACTIVE крыльями (валидация через строку)")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        duckClientDB.validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"I am flying :)\"}");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Существующий id со FIXED крыльями (валидация через ресурс)")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

        duckClientDB.validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckFlyTest/flyFixedResponse.json");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Существующий id с UNDEFINED крыльями (валидация через payload-модель)")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "UNDEFINED");

        duckClientDB.validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        FlyResponse expectedResponse = new FlyResponse("Wings are not detected :(");

        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }
}