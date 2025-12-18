package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.FlyResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Fly")
public class DuckFlyTest extends DuckClient {

    @Test(description = "Существующий id с ACTIVE крыльями (валидация через строку)")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");
        validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"I am flying :)\"}");

        // удаляем утку через БД
        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);

        // проверяем, что утка действительно удалилась
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Существующий id со FIXED крыльями (валидация через ресурс)")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "brown", 0.2, "wood", "quack", "FIXED");
        validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckFlyTest/flyFixedResponse.json");


        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);

        validateDuckNotExistsInDatabase(runner, duckId);

    }

    @Test(description = "Существующий id с UNDEFINED крыльями (валидация через payload-модель)")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = DuckClientDB.createDuckInDatabase(this, runner, "yellow", 0.121, "rubber", "quack", "UNDEFINED");
        validateDuckInDatabase(runner, duckId);

        flyDuck(runner, duckId);

        FlyResponse expectedResponse = new FlyResponse("Wings are not detected :(");
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);


        DuckClientDB.deleteDuckFromDatabase(this, runner, duckId);

        validateDuckNotExistsInDatabase(runner, duckId);

    }
}