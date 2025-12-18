package autotests.clients;

import autotests.BaseTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import io.qameta.allure.Step;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckClient extends BaseTest {

    // теперь тут только HTTP-операции, валидация ответов, работа с payload-моделями

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Step("Создаём утку через API")
    public void createDuck(@CitrusResource TestCaseRunner runner, String color, double height, String material,
                           String sound, String wingsState) {
        runner.$(http()
                .client(duckService)
                .send()
                .post(BaseTest.CREATE_DUCK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(
                        "{\"color\":\"%s\",\"height\":%s,\"material\":\"%s\",\"sound\":\"%s\",\"wingsState\":\"%s\"}",
                        color, height, material, sound, wingsState
                ))
        );
    }

    @Step("Создаем утку через модель запроса")
    public void createDuck(@CitrusResource TestCaseRunner runner, autotests.payloads.CreateRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            runner.$(http()
                    .client(duckService)
                    .send()
                    .post(BaseTest.CREATE_DUCK)
                    .message()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(json)
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DuckCreateRequest", e);
        }
    }

    // метод с прошлой домашки
    @Step("Создаем утку и извлекаем ID")
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, String color, double height,
                                         String material, String sound, String wingsState) {
        createDuck(runner, color, height, material, sound, wingsState);
        return extractIdFromResponse(runner);
    }

    // метод с прошлой домашки
    @Step("Создаём утку и извлекаем ID через модель запроса")
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, autotests.payloads.CreateRequest request) {
        createDuck(runner, request);
        return extractIdFromResponse(runner);
    }

    @Step("Удаляем утку")
    public void deleteDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .delete(BaseTest.DELETE_DUCK)
                .queryParam("id", duckId)
        );
    }

    @Step("Fly")
    public void flyDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get(BaseTest.FLY_DUCK)
                .queryParam("id", duckId)
        );
    }

    @Step("Swim")
    public void swimDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get(BaseTest.SWIM_DUCK)
                .queryParam("id", duckId)
        );
    }

    @Step("Quack")
    public void quackDuck(@CitrusResource TestCaseRunner runner, String duckId, int repetitionCount, int soundCount) {
        runner.$(http()
                .client(duckService)
                .send()
                .get(BaseTest.QUACK_DUCK)
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
                .put(BaseTest.UPDATE_DUCK)
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
                .get(BaseTest.GET_PROPERTIES)
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

    // методы для извлечения данных из ответов
    @Step("Извлекаем ID из ответа")
    public String extractIdFromResponse(@CitrusResource TestCaseRunner runner) {
        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(com.consol.citrus.dsl.JsonPathSupport.jsonPath()
                        .expression("$.id", "duckId"))
        );
        return super.extractFromContext(runner, "duckId");
    }

    @Step("Проверка id утки на чётность/нечётность")
    public void validateDuckIdParityPR(@CitrusResource TestCaseRunner runner, TestContext context,
                                       String duckId, boolean shouldBeEven) {
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

    public static String getActualIdFromTemplate(TestCaseRunner runner, TestContext context, String duckIdTemplate) {
        if (duckIdTemplate.startsWith("${") && duckIdTemplate.endsWith("}")) {
            String variableName = duckIdTemplate.substring(2, duckIdTemplate.length() - 1);
            return context.getVariable(variableName);
        }
        return duckIdTemplate;
    }

    public DuckClient() {
        super();
    }

}