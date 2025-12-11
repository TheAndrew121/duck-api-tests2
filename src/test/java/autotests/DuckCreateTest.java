package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckCreateTest extends DuckClient {

    @Test(description = "Создать утку с material = rubber")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {


        createDuck(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"@ignore@\",\"color\":\"yellow\",\"height\":0.121,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"ACTIVE\"}")
        );
    }

    @Test(description = "Создать утку с material = wood")
    @CitrusTest
    public void testCreateWoodDuck(@Optional @CitrusResource TestCaseRunner runner) {

        createDuck(runner, "brown", 0.2, "wood", "quack", "FIXED");

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"@ignore@\",\"color\":\"brown\",\"height\":0.2,\"material\":\"wood\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );
    }
}