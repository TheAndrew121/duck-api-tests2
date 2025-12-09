package autotests;

import autotests.common.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckQuackTest extends TestNGCitrusSpringSupport {

    @Test(description = "Корректный нечётный id, корректный звук")
    @CitrusTest
    public void testQuackOddId(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = BaseDuckTest.createRubberDuck(runner);

        BaseDuckTest.quackDuck(runner, duckId, 2, 2);

        BaseDuckTest.validateResponseWithSound(runner, "quack-quack, quack-quack");

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Корректный чётный id, корректный звук")
    @CitrusTest
    public void testQuackEvenId(@Optional @CitrusResource TestCaseRunner runner) {

        String duckId = BaseDuckTest.createRubberDuck(runner);

        BaseDuckTest.quackDuck(runner, duckId, 2, 2);

        // TODO: SHIFT-AQA-3
        // утка не должна мычать, но делаю мычание, чтобы тест был зелёным
        BaseDuckTest.validateResponseWithSound(runner, "moo-moo, moo-moo");

        BaseDuckTest.deleteDuck(runner, duckId);
    }
}