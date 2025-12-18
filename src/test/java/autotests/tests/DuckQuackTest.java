package autotests.tests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckQuackTest extends DuckClient {

    @Test(description = "Крякание утки с чётным id (создание через БД)")
    @CitrusTest
    public void testCreateEvenIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                     @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений
        String expectedSound = "moo-moo, moo-moo";

        // создаём утку с гарантированно чётным id через БД
        String duckId = createDuckWithParityInDataBase(runner, context, true, "moo");

        quackDuck(runner, duckId, repetitionCount, soundCount);

        validateResponseFromString(runner, HttpStatus.OK, "{\"sound\":\"" + expectedSound + "\"}");

        deleteDuckFromDatabase(runner, duckId);
    }

    @Test(description = "Крякание утки с нечётным id (создание через БД)")
    @CitrusTest
    public void testCreateOddIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                    @Optional @CitrusResource TestContext context) {
        // количество звуков и повторений можно настроить отдельно для каждого теста
        int soundCount = 2; // количество звуков
        int repetitionCount = 2; // количество повторений
        String expectedSound = "quack-quack, quack-quack";

        String duckId = createDuckWithParityInDataBase(runner, context, false, "quack");

        quackDuck(runner, duckId, repetitionCount, soundCount);

        validateResponseFromString(runner, HttpStatus.OK, "{\"sound\":\"" + expectedSound + "\"}");

        deleteDuckFromDatabase(runner, duckId);
    }


    private String createDuckWithParityInDataBase(TestCaseRunner runner, TestContext context,
                                                  boolean shouldBeEven, String sound) {
        for (int attempt = 1; attempt <= 2; attempt++) {

            String duckIdTemplate = createDuckInDatabase(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

            String actualDuckId = getActualDuckIdFromContext(context, duckIdTemplate);

            long id = Long.parseLong(actualDuckId);
            boolean isEven = id % 2 == 0;

            if (isEven == shouldBeEven) {
                System.out.println("Создана утка в БД с " + (shouldBeEven ? "чётным" : "нечётным") +
                        " ID: " + actualDuckId + " (попытка: " + attempt + ")");

                saveDuckIdToContext(runner, actualDuckId);

                return actualDuckId;
            }

            System.out.println("ID " + actualDuckId + " не подходит по четности. Попытка " + attempt);
        }

        throw new RuntimeException("Не удалось создать утку в БД с " +
                (shouldBeEven ? "чётным" : "нечётным") + " ID после 2 попыток");
    }

    private String getActualDuckIdFromContext(TestContext context, String duckIdTemplate) {

        if (duckIdTemplate.startsWith("${") && duckIdTemplate.endsWith("}")) {
            String variableName = duckIdTemplate.substring(2, duckIdTemplate.length() - 1);
            return context.getVariable(variableName);
        }
        return duckIdTemplate;
    }

    private void saveDuckIdToContext(TestCaseRunner runner, String duckId) {
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                context.setVariable("duckId", duckId);
            }
        });
    }

}