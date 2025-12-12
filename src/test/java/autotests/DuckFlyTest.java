package autotests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.FlyResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckFlyTest extends DuckClient {

    @Test(description = "Существующий id с ACTIVE крыльями (валидация через строку)")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {
        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        String duckId = createDuckAndExtractId(runner, req);

        flyDuck(runner, duckId);

        // валидация через строку
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"I am flying :)\"}");

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Существующий id со FIXED крыльями (валидация через ресурс)")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("brown", 0.2, "wood", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        flyDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckFlyTest/flyFixedResponse.json");

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Существующий id с UNDEFINED крыльями (валидация через payload-модель)")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {
        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "UNDEFINED");
        String duckId = createDuckAndExtractId(runner, req);

        flyDuck(runner, duckId);

        FlyResponse expectedResponse = new FlyResponse("Wings are not detected :(");
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }
}