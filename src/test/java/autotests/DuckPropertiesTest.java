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

public class DuckPropertiesTest extends DuckClient {

    @Test(description = "Получение свойств утки с material = wood")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "brown", 0.2, "wood", "quack", "FIXED");

        getDuckProperties(runner, duckId);

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
        );

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Cвойства утки с material = rubber")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "red", 50, "rubber", "quack", "FIXED");

        getDuckProperties(runner, duckId);

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"red\",\"height\":5000.0,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }
}