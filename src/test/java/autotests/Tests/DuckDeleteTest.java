package autotests.Tests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.DeleteResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckDeleteTest extends DuckClient {

    @Test(description = "Удаление утки (валидация через строку)")
    @CitrusTest
    public void testDeleteDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {
        // класс-модель для создания утки
        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        String duckId = createDuckAndExtractId(runner, req);

        deleteDuck(runner, duckId);

        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Удаление утки (валидация через ресурс)")
    @CitrusTest
    public void testDeleteDuckResourceValidation(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("brown", 0.2, "wood", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        deleteDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckDeleteTest/deleteResponse.json");
    }

    @Test(description = "Удаление утки (валидация через payload-модель)")
    @CitrusTest
    public void testDeleteDuckPayloadValidation(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("red", 50, "rubber", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        deleteDuck(runner, duckId);

        // через payload-модель
        DeleteResponse expectedResponse = new DeleteResponse("Duck is deleted");
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);
    }
}