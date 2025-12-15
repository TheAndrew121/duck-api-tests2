package autotests.clients;

import autotests.BaseTest;
import autotests.payloads.CreateRequest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.context.TestContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckClient extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static String extractIdFromResponse(BaseTest baseTest, @CitrusResource TestCaseRunner runner) {
        runner.$(http()
                .client(baseTest.duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(jsonPath().expression("$.id", "duckId"))
        );
        return "${duckId}";
    }

    public static void validateResponse(BaseTest baseTest, @CitrusResource TestCaseRunner runner, HttpStatus status, String expectedBody) {
        runner.$(http()
                .client(baseTest.duckService)
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    public static void validateResponseWithMessage(BaseTest baseTest, @CitrusResource TestCaseRunner runner, HttpStatus status, String message) {
        validateResponse(baseTest, runner, status, "{\"message\":\"" + message + "\"}");
    }

    // новый createDuck принимает модель запроса и отправляет json body.
    public void createDuck(@CitrusResource TestCaseRunner runner, CreateRequest request) {
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

    // метод из прошлой домашки пока оставлю
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, String color, double height,
                                         String material, String sound, String wingsState) {
        createDuck(runner, color, height, material, sound, wingsState);
        return extractIdFromResponse(this, runner);
    }

    public void createDuck(@CitrusResource TestCaseRunner runner, String color, double height, String material,
                           String sound, String wingsState) {
        super.createDuck(runner, color, height, material, sound, wingsState);
    }

    public void flyDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", duckId)
        );
    }

    public void swimDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", duckId)
        );
    }

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

    public void getDuckProperties(@CitrusResource TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client(duckService)
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", duckId)
        );
    }

    public void deleteDuck(@CitrusResource TestCaseRunner runner, String duckId) {
        super.deleteDuck(runner, duckId);
    }



    // новые методы для 2 части 29 домашки

    // 1) валидация через String
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

    // 2) валидация через ресурс
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

    // 3) валидация через payload-модель: сериализуем модель в json и сравниваем как строку.
    public void validateResponseFromPayload(@CitrusResource TestCaseRunner runner, HttpStatus status, Object expectedPayload) {
        try {
            String json = objectMapper.writeValueAsString(expectedPayload);
            validateResponseFromString(runner, status, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize expected payload", e);
        }
    }

    // для создания утки и получения id через модель запроса
    public String createDuckAndExtractId(@CitrusResource TestCaseRunner runner, CreateRequest request) {
        createDuck(runner, request);
        return extractIdFromResponse(this, runner);
    }

    // метод проверки id на чётность
    public static void isDuckIdEven(TestCaseRunner runner) {
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                String duckId = context.getVariable("duckId");
                long id = Long.parseLong(duckId);

                if (id % 2 == 0) {
                    System.out.println("Создана утка с чётным id: " + id);
                    // Можно установить переменную для дальнейшего использования
                    context.setVariable("duckType", "even");
                } else {
                    System.out.println("Создана утка с нечётным id: " + id);
                    context.setVariable("duckType", "odd");
                }
            }
        });



    };


}
