package autotests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.SwimResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

// TODO: SHIFT-AQA-3
public class DuckSwimTest extends DuckClient {

    @Test(description = "Существующий id (валидация через ресурс)")
    @CitrusTest
    public void testSwimExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        String duckId = createDuckAndExtractId(runner, req);

        swimDuck(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.NOT_FOUND, "DuckSwimTest/swimNotFoundResponse.json");

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Несуществующий id (валидация через payload-модель)")
    @CitrusTest
    public void testSwimNonExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", "-1")
        );

        // Валидация через payload-модель
        SwimResponse expectedResponse = new SwimResponse("Paws are not found ((((");
        validateResponseFromPayload(runner, HttpStatus.NOT_FOUND, expectedResponse);
    }

    @Test(description = "Существующий id (валидация через строку)")
    @CitrusTest
    public void testSwimExistingDuckStringValidation(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        String duckId = createDuckAndExtractId(runner, req);

        swimDuck(runner, duckId);

        // валидация через строку
        validateResponseFromString(runner, HttpStatus.NOT_FOUND, "{\"message\":\"Paws are not found ((((\"}");

        deleteDuck(runner, duckId);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }
}