package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class BaseDuckTest {

    // Метод только для создания утки без валидации
    public static void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {

        runner.$(http()
                .client("http://localhost:2222")
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

    // отдельный метод для валидации ответа создания утки
    public static String validateDuckCreation(TestCaseRunner runner, String color, double height,
                                              String material, String sound, String wingsState) {

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .extract(jsonPath().expression("$.id", "duckId"))
                .body(String.format(
                        "{\"id\":${duckId},\"color\":\"%s\",\"height\":%s,\"material\":\"%s\",\"sound\":\"%s\",\"wingsState\":\"%s\"}",
                        color, height, material, sound, wingsState
                ))
        );

        return "${duckId}";
    }

    // специальной метод для создания утки с определённым чётным/нечётным id
    public static String createDuckWithCertainID(TestCaseRunner runner, TestContext context,
                                                 boolean shouldBeEven, String sound) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            BaseDuckTest.createDuck(runner, "red", 0.121, "rubber", "quack", "ACTIVE");
            BaseDuckTest.validateDuckCreation(runner, "red", 0.121, "rubber", "quack", "ACTIVE");

            // проверка id на чётность
            runner.run(new AbstractTestAction() {
                @Override
                public void doExecute(TestContext ctx) {
                    long id = Long.parseLong(ctx.getVariable("duckId"));
                    ctx.setVariable("isIdEven", id % 2 == 0);
                    ctx.setVariable("currentDuckId", String.valueOf(id));
                }
            });

            boolean isEven = Boolean.parseBoolean(context.getVariable("isIdEven"));
            String currentDuckId = context.getVariable("currentDuckId");

            if (isEven == shouldBeEven) {
                System.out.println("Создана утка с " + (shouldBeEven ? "чётным" : "нечётным") +
                        " ID: " + currentDuckId + " (попытка: " + attempt + ")");
                return currentDuckId;
            }

            deleteDuck(runner, currentDuckId);
            System.out.println("ID " + currentDuckId + " не подходит по четности. Попытка " + attempt);
        }

        throw new RuntimeException("Не удалось создать утку с " +
                (shouldBeEven ? "чётным" : "нечётным") + " ID после 2 попыток");
    }

    public static void deleteDuck(TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", duckId)
        );
    }

    public static void flyDuck(TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", duckId)
        );
    }

    public static void swimDuck(TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/swim")
                .queryParam("id", duckId)
        );
    }

    public static void quackDuck(TestCaseRunner runner, String duckId, int repetitionCount, int soundCount) {
        runner.$(
                http()
                        .client("http://localhost:2222")
                        .send()
                        .get("/api/duck/action/quack")
                        .queryParam("id", duckId)
                        .queryParam("repetitionCount", String.valueOf(repetitionCount))
                        .queryParam("soundCount", String.valueOf(soundCount))
        );
    }

    public static void updateDuck(TestCaseRunner runner, String duckId, String color, String height,
                                  String material, String sound, String wingsState) {
        runner.$(http()
                .client("http://localhost:2222")
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

    public static void getDuckProperties(TestCaseRunner runner, String duckId) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", duckId)
        );
    }

    public static void validateResponse(TestCaseRunner runner, String expectedBody) {
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    public static void validateResponseWithMessage(TestCaseRunner runner, HttpStatus status, String message) {
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(status)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"" + message + "\"}")
        );
    }

    public static void validateResponseWithSound(TestCaseRunner runner, String sound) {
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"sound\":\"" + sound + "\"}")
        );
    }

    public static void validatePropertiesResponse(TestCaseRunner runner, String color, double originalHeight,
                                                  String material, String sound, String wingsState) {
        // сразу умножаем полученную высоту на 100
        double multipliedHeight = originalHeight * 100;

        String expectedBody = String.format(
                "{\"color\":\"%s\",\"height\":%s,\"material\":\"%s\",\"sound\":\"%s\",\"wingsState\":\"%s\"}",
                color, multipliedHeight, material, sound, wingsState
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedBody)
        );
    }

    public static void validateDuckIdEvenness(TestCaseRunner runner, boolean shouldBeEven) {
        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                String duckId = context.getVariable("duckId");
                long id = Long.parseLong(duckId);
                boolean isEven = (id % 2 == 0);

                System.out.println("Проверка чётности ID: " + id);
                System.out.println("ID чётный: " + isEven);
                System.out.println("Ожидается чётный: " + shouldBeEven);

                if (isEven != shouldBeEven) {
                    throw new AssertionError(
                            String.format("Несоответствие чётности ID! ID: %d, чётный: %s, ожидалось: %s",
                                    id, isEven, shouldBeEven)
                    );
                }

                // Устанавливаем переменную для дальнейшего использования
                context.setVariable("duckType", isEven ? "even" : "odd");

                System.out.println("Валидация чётности пройдена успешно");
            }
        });
    }

    // получение ID утки как числа
    public static long getDuckIdAsLong(TestCaseRunner runner) {
        final long[] idHolder = new long[1];

        runner.run(new AbstractTestAction() {
            @Override
            public void doExecute(TestContext context) {
                String duckId = context.getVariable("duckId");
                idHolder[0] = Long.parseLong(duckId);
            }
        });

        return idHolder[0];
    }


}