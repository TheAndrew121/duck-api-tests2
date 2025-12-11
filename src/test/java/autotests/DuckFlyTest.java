package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckFlyTest extends DuckClient {

    @Test(description = "Существующий id с ACTIVE крыльями")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        flyDuck(runner, duckId);

        validateResponseWithMessage(runner, HttpStatus.OK, "I am flying :)");

        deleteDuck(runner, duckId);

        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Существующий id со FIXED крыльями")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "brown", 0.2, "wood", "quack", "FIXED");

        flyDuck(runner, duckId);

        validateResponseWithMessage(runner, HttpStatus.OK, "I can not fly :C");

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Существующий id с UNDEFINED крыльями")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "UNDEFINED");

        flyDuck(runner, duckId);

        validateResponseWithMessage(runner, HttpStatus.OK, "Wings are not detected :(");

        deleteDuck(runner, duckId);
        validateResponseWithMessage(runner, HttpStatus.OK, "Duck is deleted");
    }
}