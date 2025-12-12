package autotests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.PropertiesResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

// TODO: SHIFT-AQA-1
public class DuckPropertiesTest extends DuckClient {

    @Test(description = "Получение свойств утки с material = wood (валидация через resource)")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner) {
        CreateRequest req = new CreateRequest("brown", 0.2, "wood", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        getDuckProperties(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckPropertiesTest/woodPropertiesResponse.json");

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Свойства утки с material = rubber (валидация через payload-модель)")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner) {
        CreateRequest req = new CreateRequest("red", 50, "rubber", "quack", "FIXED");
        String duckId = createDuckAndExtractId(runner, req);

        getDuckProperties(runner, duckId);

        PropertiesResponse expected = new PropertiesResponse("red", 5000.0, "rubber", "quack", "FIXED");
        validateResponseFromPayload(runner, HttpStatus.OK, expected);

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }
}
