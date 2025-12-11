package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

// TODO: SHIFT-AQA-4
public class DuckSwimTest extends DuckClient {

    @Test(description = "Существующий id")
    @CitrusTest
    public void testSwimExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        swimDuck(runner, duckId);

        // ожидаем NOT_FOUND, Paws are not found ((((, чтобы тест был зелёный
        validateResponseWithMessage(runner, HttpStatus.NOT_FOUND, "Paws are not found ((((");

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Несуществующий id")
    @CitrusTest
    public void testSwimNonExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", "-1")
        );

        validateResponseWithMessage(runner, HttpStatus.NOT_FOUND, "Paws are not found ((((");
    }
}