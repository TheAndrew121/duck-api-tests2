package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import autotests.payloads.UpdateResponse;
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
@Feature("Update")
public class DuckUpdateTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Изменить цвет и высоту уточки (валидация через payload-модель)")
    @CitrusTest
    public void testUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "FIXED");

        updateDuck(runner, duckId, "BLACK", "0.555", "rubber", "quack", "FIXED");

        UpdateResponse expectedResponse = new UpdateResponse(String.format("Duck with id = %s is updated", duckId));

        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);


        duckClientDB.validateDuckInDatabase(runner, duckId, "BLACK", "0.555", "rubber", "quack", "FIXED");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Изменить цвет и звук уточки (валидация через ресурс)")
    @CitrusTest
    public void testUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        updateDuck(runner, duckId, "blue", "0.1", "rubber", "quack-quack!", "ACTIVE");


        duckClientDB.validateDuckInDatabase(runner, duckId, "blue", "0.1", "rubber", "quack-quack!", "ACTIVE");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);

        duckClientDB.validateDuckNotExistsInDatabase(runner, duckId);
    }
}