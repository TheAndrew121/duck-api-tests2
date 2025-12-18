package autotests.clients;

import autotests.BaseTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import io.qameta.allure.Step;
import org.springframework.stereotype.Component;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;

@Component
public class DuckClientDB extends BaseTest {

    // клиент для работы с базой данных

    @Step("Создаём утку в БД")
    public String createDuckInDatabase(@CitrusResource TestCaseRunner runner, String color, double height,
                                       String material, String sound, String wingsState) {
        getNextDuckIdFromDatabase(runner);
        String sql = String.format(
                "INSERT INTO DUCK (id, color, height, material, sound, wings_state) " +
                        "VALUES (%s, '%s', %s, '%s', '%s', '%s')",
                "${nextDuckId}", color, height, material, sound, wingsState
        );
        runner.$(sql(testDb)
                .statement(sql));
        return "${nextDuckId}";
    }

    @Step("Удаляем утку из БД")
    public void deleteDuckFromDatabase(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(sql(testDb)
                .statement("DELETE FROM DUCK WHERE ID = " + duckId));
    }

    @Step("Получаем следующий id для утки из БД")
    public void getNextDuckIdFromDatabase(@CitrusResource TestCaseRunner runner) {
        runner.$(query(testDb)
                .statement("SELECT CASE WHEN MAX(id) IS NULL THEN 1 ELSE MAX(id) + 1 END as next_id FROM DUCK")
                .extract("NEXT_ID", "nextDuckId"));
    }

    @Step("Проверяем данные утки в БД")
    public void validateDuckInDatabase(@CitrusResource TestCaseRunner runner, String idVariable, String color,
                                       String height, String material, String sound, String wingsState) {
        runner.$(query(testDb)
                .statement("SELECT * FROM DUCK WHERE ID = " + idVariable)
                .validate("COLOR", color)
                .validate("HEIGHT", height)
                .validate("MATERIAL", material)
                .validate("SOUND", sound)
                .validate("WINGS_STATE", wingsState));
    }

    @Step("Проверяем наличие утки в БД")
    public void validateDuckInDatabase(@CitrusResource TestCaseRunner runner, String idVariable) {
        runner.$(query(testDb)
                .statement("SELECT COUNT(*) as count FROM DUCK WHERE ID = " + idVariable)
                .validate("COUNT", "1"));
    }

    @Step("Проверяем отсутствие утки в БД")
    public void validateDuckNotExistsInDatabase(@CitrusResource TestCaseRunner runner, String idVariable) {
        runner.$(query(testDb)
                .statement("SELECT COUNT(*) as count FROM DUCK WHERE ID = " + idVariable)
                .validate("COUNT", "0"));
    }
}