package autotests.tests;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Quack")
public class DuckQuackTest extends DuckClient {

    @Autowired
    private DuckClientDB duckClientDB;

    @Test(description = "Крякание утки с чётным id (создание через БД)")
    @CitrusTest
    public void testCreateEvenIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                     @Optional @CitrusResource TestContext context) {
        int soundCount = 2;
        int repetitionCount = 2;
        String expectedSound = "moo-moo, moo-moo";

        String duckId = createDuckWithParityInDataBase(runner, context, true, "moo");

        quackDuck(runner, duckId, repetitionCount, soundCount);
        validateResponseFromString(runner, HttpStatus.OK, "{\"sound\":\"" + expectedSound + "\"}");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);
    }

    @Test(description = "Крякание утки с нечётным id (создание через БД)")
    @CitrusTest
    public void testCreateOddIdDuck(@Optional @CitrusResource TestCaseRunner runner,
                                    @Optional @CitrusResource TestContext context) {
        int soundCount = 2;
        int repetitionCount = 2;
        String expectedSound = "quack-quack, quack-quack";

        String duckId = createDuckWithParityInDataBase(runner, context, false, "quack");

        quackDuck(runner, duckId, repetitionCount, soundCount);
        validateResponseFromString(runner, HttpStatus.OK, "{\"sound\":\"" + expectedSound + "\"}");

        duckClientDB.deleteDuckFromDatabase(runner, duckId);
    }

    private String createDuckWithParityInDataBase(TestCaseRunner runner, TestContext context,
                                                  boolean shouldBeEven, String sound) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            String duckIdTemplate = duckClientDB.createDuckInDatabase(runner, "yellow", 0.121, "rubber", sound, "ACTIVE");
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
        runner.variable("duckId", duckId);
    }
}