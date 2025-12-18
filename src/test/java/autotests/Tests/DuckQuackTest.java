package autotests.Tests;

import autotests.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckQuackTest extends TestNGCitrusSpringSupport {

    @Test(description = "Крякание утки с чётным id")
    @CitrusTest
    public void testCreateEvenIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                     @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений

        // создаём утку с гарантированно чётным id
        String duckId = BaseDuckTest.createDuckWithCertainID(runner, context, true);

        BaseDuckTest.quackDuck(runner, duckId, repetitionCount, soundCount);

        BaseDuckTest.validateResponseWithSound(runner, "moo-moo, moo-moo");

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Крякание утки с нечётным id")
    @CitrusTest
    public void testCreateOddIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                    @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений

        // создаём утку с гарантированно нечётным id
        String duckId = BaseDuckTest.createDuckWithCertainID(runner, context, false);

        BaseDuckTest.quackDuck(runner, duckId, repetitionCount, soundCount);

        BaseDuckTest.validateResponseWithSound(runner, "quack-quack, quack-quack");

        BaseDuckTest.deleteDuck(runner, duckId);
    }
}