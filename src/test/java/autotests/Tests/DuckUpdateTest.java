package autotests.Tests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.UpdateResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckUpdateTest extends DuckClient {

    @Test(description = "Изменить цвет и высоту уточки (валидация через payload-модель)")
    @CitrusTest
    public void testUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        updateDuck(runner, duckId, "BLACK", "0.555", "rubber", "quack", "FIXED");

        // через payload-модель
        UpdateResponse expectedResponse = new UpdateResponse(String.format("Duck with id = %s is updated", duckId));
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Изменить цвет и звук уточки (валидация через ресурс)")
    @CitrusTest
    public void testUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        String duckId = createDuckAndExtractId(runner, req);

        updateDuck(runner, duckId, "blue", "0.1", "rubber", "quack-quack!", "ACTIVE");

        // через ресурс
        validateResponseFromResource(runner, HttpStatus.OK, "DuckUpdateTest/updateResponse.json");

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }
}