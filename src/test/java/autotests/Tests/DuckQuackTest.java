package autotests.Tests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckQuackTest extends DuckClient {

    @Test(description = "Крякание утки с чётным id")
    @CitrusTest
    public void testCreateEvenIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                     @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений

        // создаём утку с гарантированно чётным id
        String duckId = createDuckWithCertainID(this, runner, context, true, "moo");

        // тестируем кряканье
        quackDuck(runner, duckId, repetitionCount, soundCount);

        // проверяем ответ
        validateQuackResponse(this, runner, "moo-moo, moo-moo");

        // проверяем, что ID действительно чётный
        validateDuckIdEvenness(runner, true);

        // удаляем утку
        deleteDuck(runner, duckId);
    }

    @Test(description = "Крякание утки с нечётным id")
    @CitrusTest
    public void testCreateOddIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                    @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений

        // создаём утку с гарантированно нечётным id
        String duckId = createDuckWithCertainID(this, runner, context, false, "quack");

        quackDuck(runner, duckId, repetitionCount, soundCount);

        validateQuackResponse(this, runner, "quack-quack, quack-quack");

        // проверяем, что ID действительно нечётный
        validateDuckIdEvenness(runner, false);

        deleteDuck(runner, duckId);
    }

}