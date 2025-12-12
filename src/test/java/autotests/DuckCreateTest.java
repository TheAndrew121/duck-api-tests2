package autotests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.CreateResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckCreateTest extends DuckClient {

    @Test(description = "Создать утку с material = rubber (валидация через payload модель)")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", "quack", "ACTIVE");
        createDuck(runner, req);

        // payload-модель для проверки
        CreateResponse expected = new CreateResponse("@ignore@", "yellow", 0.121, "rubber", "quack", "ACTIVE");
        validateResponseFromPayload(runner, HttpStatus.OK, expected);
    }

    @Test(description = "Создать утку с material = wood (валидация через строку)")
    @CitrusTest
    public void testCreateWoodDuck(@Optional @CitrusResource TestCaseRunner runner) {

        CreateRequest req = new CreateRequest("brown", 0.2, "wood", "quack", "FIXED");
        createDuck(runner, req);

        String expectedBody = "{\"id\":\"@ignore@\",\"color\":\"brown\",\"height\":0.2,\"material\":\"wood\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}";
        validateResponseFromString(runner, HttpStatus.OK, expectedBody);
    }
}
