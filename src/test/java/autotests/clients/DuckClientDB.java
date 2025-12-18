package autotests.clients;

import autotests.BaseTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import io.qameta.allure.Step;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;


public class DuckClientDB extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Step("Создаём утку в БД")
    public static String createDuckInDatabase(BaseTest baseTest, @CitrusResource TestCaseRunner runner, String color, double height,
                                              String material, String sound, String wingsState) {
        // получаем следующий доступный id
        baseTest.getNextDuckIdFromDatabase(runner);

        // переменная Citrus для получения id
        String sql = String.format(
                "INSERT INTO DUCK (id, color, height, material, sound, wings_state) " +
                        "VALUES (%s, '%s', %s, '%s', '%s', '%s')",
                "${nextDuckId}", color, height, material, sound, wingsState
        );
        runner.$(sql(baseTest.testDb)
                .statement(sql));
        return "${nextDuckId}";
    }

    @Step("Удаляем утку из БД")
    public static void deleteDuckFromDatabase(BaseTest baseTest, @CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(sql(baseTest.testDb)
                .statement("DELETE FROM DUCK WHERE ID = " + duckId));
    }
}
