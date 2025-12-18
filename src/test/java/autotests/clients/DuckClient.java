package autotests.clients;

import autotests.tests.DuckPropertiesTest;
import autotests.BaseTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.consol.citrus.actions.ExecuteSQLQueryAction.Builder.query;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckClient extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Step("Проверка id утки на чётность/нечётность")
    protected static void validateDuckIdParity(DuckPropertiesTest duckPropertiesTest, @CitrusResource TestCaseRunner runner, @CitrusResource TestContext context, String duckId) {

        runner.$(query(duckPropertiesTest.testDb)
                .statement("SELECT id FROM DUCK WHERE id = " + duckId)
                .extract("ID", "duckIdValue"));

        String extractedId = context.getVariable("duckIdValue");
        long idValue = Long.parseLong(extractedId);

        boolean isEven = idValue % 2 == 0;

        if (idValue <= 0) {
            throw new AssertionError("Invalid duck ID: " + idValue);
        }

        System.out.println("Duck ID: " + idValue + " is " + (isEven ? "even" : "odd"));
    }

    @Step("Создаем утку через модель запроса")
    public void createDuck(@CitrusResource TestCaseRunner runner, autotests.payloads.CreateRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            runner.$(http()
                    .client(duckService)
                    .send()
                    .post("/api/duck/create")
                    .message()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(json)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DuckCreateRequest", e);
        }
    }

    @Step("Создаем утку и извлекаем ID")
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, String color, double height,
                                         String material, String sound, String wingsState) {
        createDuck(runner, color, height, material, sound, wingsState);
        return extractIdFromResponse(runner);
    }

    @Step("Создаём утку и извлекаем ID через модель запроса")
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, autotests.payloads.CreateRequest request) {
        createDuck(runner, request);
        return extractIdFromResponse(runner);
    }

    @Step("Fly")
    public void flyDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", duckId)
        );
    }

    @Step("Swim")
    public void swimDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", duckId)
        );
    }

    @Step("Quack")
    public void quackDuck(@CitrusResource TestCaseRunner runner, String duckId, int repetitionCount, int soundCount) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", duckId)
                .queryParam("repetitionCount", String.valueOf(repetitionCount))
                .queryParam("soundCount", String.valueOf(soundCount))
        );
    }

    @Step("Update")
    public void updateDuck(@CitrusResource TestCaseRunner runner, String duckId, String color, String height,
                           String material, String sound, String wingsState) {
        runner.$(http()
                .client(duckService)
                .send()
                .put("/api/duck/update")
                .queryParam("id", duckId)
                .queryParam("color", color)
                .queryParam("height", height)
                .queryParam("material", material)
                .queryParam("sound", sound)
                .queryParam("wingsState", wingsState)
        );
    }

    @Step("Get Properties")
    public void getDuckProperties(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", duckId)
        );
    }


    @Step("Валидируем ответ через строку")
    public void validateResponseFromString(@CitrusResource TestCaseRunner runner, HttpStatus status, String expectedBody) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    @Step("Валидируем ответ через ресурс")
    public void validateResponseFromResource(@CitrusResource TestCaseRunner runner, HttpStatus status, String resourcePath) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new ClassPathResource(resourcePath))
        );
    }

    @Step("Валидируем ответ через payload-модель")
    public void validateResponseFromPayload(@CitrusResource TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        try {
            String json = objectMapper.writeValueAsString(expectedPayload);
            validateResponseFromString(runner, status, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize expected payload", e);
        }
    }
}