package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.context.ContextConfiguration;

import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@ContextConfiguration(classes = {autotests.config.EndpointConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    @Autowired
    public com.consol.citrus.http.client.HttpClient duckService;

    @Autowired
    public SingleConnectionDataSource testDb;

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

    @Step("Проверяем отсутствие утки в БД")
    public void validateDuckNotExistsInDatabase(@CitrusResource TestCaseRunner runner, String idVariable) {
        runner.$(query(testDb)
                .statement("SELECT COUNT(*) as count FROM DUCK WHERE ID = " + idVariable)
                .validate("COUNT", "0"));
    }

}