package autotests.clients;

import autotests.BaseTest;
import autotests.Tests.DuckQuackTest;
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

    protected static void extractDuckIdFromResponse(DuckQuackTest duckQuackTest, TestCaseRunner runner) {
        runner.$(http()
                .client(duckQuackTest.duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(jsonPath().expression("$.id", "duckId"))
        );
    }



    // проверка чётности id утки
    protected static void validateDuckIdEvenness(TestCaseRunner runner, boolean shouldBeEven) {
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                String duckId = context.getVariable("duckId");
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


    protected static void validateQuackResponse(DuckQuackTest duckQuackTest, TestCaseRunner runner, String expectedSound) {
        String expectedBody = "{\"sound\":\"" + expectedSound + "\"}";
        runner.$(http()
                .client(duckQuackTest.duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    // специальный метод для создания утки с гарантированно чётным или нечётным id
    protected static String createDuckWithCertainID(DuckQuackTest duckQuackTest, TestCaseRunner runner, TestContext context,
                                                    boolean shouldBeEven, String sound) {
        for (int attempt = 1; attempt <= 2; attempt++) {

            CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", sound, "ACTIVE");
            duckQuackTest.createDuck(runner, req);

            // извлечение id
            DuckClient.extractDuckIdFromResponse(duckQuackTest, runner);

            // проверяем чётность id
            runner.run(new AbstractTestAction() {
                @Override
                public void doExecute(TestContext ctx) {
                    String duckId = ctx.getVariable("duckId");
                    long id = Long.parseLong(duckId);
                    ctx.setVariable("isIdEven", id % 2 == 0);
                    ctx.setVariable("currentDuckId", duckId);
                }
            });

            boolean isEven = Boolean.parseBoolean(context.getVariable("isIdEven"));
            String currentDuckId = context.getVariable("currentDuckId");

            if (isEven == shouldBeEven) {
                System.out.println("Создана утка с " + (shouldBeEven ? "чётным" : "нечётным") +
                        " ID: " + currentDuckId + " (попытка: " + attempt + ")");
                return currentDuckId;
            }

            duckQuackTest.deleteDuck(runner, currentDuckId);
            System.out.println("ID " + currentDuckId + " не подходит по четности. Попытка " + attempt);
        }

        throw new RuntimeException("Не удалось создать утку с " +
                (shouldBeEven ? "чётным" : "нечётным") + " ID после 2 попыток");
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

}
