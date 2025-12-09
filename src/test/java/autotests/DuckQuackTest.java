package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckQuackTest extends DuckClient {

    @Test(description = "Корректный нечётный id, корректный звук")
    @CitrusTest
    public void testQuackOddId(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        quackDuck(runner, duckId, 2, 2);

        validateResponseWithSound(runner, "quack-quack, quack-quack");

        deleteDuck(runner, duckId);
    }

    @Test(description = "Корректный чётный id, корректный звук")
    @CitrusTest
    public void testQuackEvenId(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        quackDuck(runner, duckId, 2, 2);

        // TODO: SHIFT-AQA-3
        validateResponseWithSound(runner, "moo-moo, moo-moo");

        deleteDuck(runner, duckId);
    }
}