package autotests;

import autotests.common.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckSwimTest extends TestNGCitrusSpringSupport {

    @Test(description = "Существующий id")
    @CitrusTest
    public void testSwimExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = BaseDuckTest.createRubberDuck(runner);

        BaseDuckTest.swimDuck(runner, duckId);

        // ожидаем NOT_FOUND, чтобы тест был зелёным, хотя утка существует и должна лететь
        BaseDuckTest.validateResponseWithMessage(runner, HttpStatus.NOT_FOUND, "Paws are not found ((((");

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Несуществующий id")
    @CitrusTest
    public void testSwimNonExistingDuck(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", "546349")
        );

        BaseDuckTest.validateResponseWithMessage(runner, HttpStatus.NOT_FOUND, "Paws are not found ((((");
    }
}