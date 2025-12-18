package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLAction.Builder.sql;
import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {autotests.config.EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    protected com.consol.citrus.http.client.HttpClient duckService;

    @Autowired
    protected SingleConnectionDataSource testDb;

    protected static void validateDuckIdParityPR(TestCaseRunner runner, TestContext context,
                                                 String duckId, boolean shouldBeEven) {
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext ctx) {
                long id = Long.parseLong(duckId);
                boolean isEven = id % 2 == 0;

                System.out.println("Проверка чётности ID: " + id);
                System.out.println("ID чётный: " + isEven);
                System.out.println("Ожидается чётный: " + shouldBeEven);

                if (isEven != shouldBeEven) {
                    throw new AssertionError(
                            String.format("Несоответствие чётности ID! ID: %d, чётный: %s, ожидалось: %s",
                                    id, isEven, shouldBeEven)
                    );
                }

                System.out.println("Валидация чётности пройдена успешно");
            }
        });
    }

    protected static String getActualIdFromTemplate(TestCaseRunner runner, TestContext context, String duckIdTemplate) {
        if (duckIdTemplate.startsWith("${") && duckIdTemplate.endsWith("}")) {
            String variableName = duckIdTemplate.substring(2, duckIdTemplate.length() - 1);
            return context.getVariable(variableName);
        }
        return duckIdTemplate;
    }

    @Step("Создаём утку")
    public void createDuck(@CitrusResource TestCaseRunner runner, String color, double height, String material,
                           String sound, String wingsState) {
        runner.$(http()
                .client(duckService)
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(
                        "{\"color\":\"%s\",\"height\":%s,\"material\":\"%s\",\"sound\":\"%s\",\"wingsState\":\"%s\"}",
                        color, height, material, sound, wingsState
                ))
        );
    }

    @Step("Извлекаем ID из ответа")
    public String extractIdFromResponse(@CitrusResource TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(jsonPath().expression("$.id", "duckId"))
        );
        return "${duckId}";
    }

    @Step("Удаляем утку")
    public void deleteDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", duckId)
        );
    }

    @Step("Валидируем ответ")
    public void validateResponse(@CitrusResource TestCaseRunner runner, HttpStatus status, String expectedBody) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    @Step("Валидируем ответ с сообщением")
    public void validateResponseWithMessage(@CitrusResource TestCaseRunner runner, HttpStatus status, String message) {
        validateResponse(runner, status, "{\"message\":\"" + message + "\"}");
    }



    // Методы для работы с БД

    @Step("Создаём утку в БД")
    public String createDuckInDatabase(@CitrusResource TestCaseRunner runner, String color, double height,
                                       String material, String sound, String wingsState) {
        // получаем следующий доступный id
        getNextDuckIdFromDatabase(runner);

        // переменная Citrus для получения id
        String sql = String.format(
                "INSERT INTO DUCK (id, color, height, material, sound, wings_state) " +
                        "VALUES (%s, '%s', %s, '%s', '%s', '%s')",
                "${nextDuckId}", color, height, material, sound, wingsState
        );
        runner.$(sql(testDb)
                .statement(sql));
        return "${nextDuckId}";
    }

    @Step("Получаем следующий id для утки из БД")
    public void getNextDuckIdFromDatabase(@CitrusResource TestCaseRunner runner) {
        // запрашиваем следующий id и сохраняем в переменную
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

    @Step("Удаляем утку из БД")
    public void deleteDuckFromDatabase(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(sql(testDb)
                .statement("DELETE FROM DUCK WHERE ID = " + duckId));
    }

    @Step("Проверяем отсутствие утки в БД")
    public void validateDuckNotExistsInDatabase(@CitrusResource TestCaseRunner runner, String idVariable) {
        runner.$(query(testDb)
                .statement("SELECT COUNT(*) as count FROM DUCK WHERE ID = " + idVariable)
                .validate("COUNT", "0"));
    }

}