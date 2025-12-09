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

public class DuckUpdateTest extends DuckClient {

    @Test(description = "Изменить цвет и высоту уточки")
    @CitrusTest
    public void testUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "FIXED");

        updateDuck(runner, duckId, "BLACK", "0.555", "rubber", "quack", "FIXED");

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format("{\"message\":\"Duck with id = %s is updated\"}", duckId))
        );

        deleteDuck(runner, duckId);
    }

    @Test(description = "Изменить цвет и звук уточки")
    @CitrusTest
    public void testUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        updateDuck(runner, duckId, "blue", "0.1", "rubber", "quack-quack!", "ACTIVE");

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format("{\"message\":\"Duck with id = %s is updated\"}", duckId))
        );

        deleteDuck(runner, duckId);
    }
}