package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class BaseDuckTest {

    public static String createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {

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
        // извлечение id
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"@ignore@\",\"color\":\"" + color + "\",\"height\":" + height + ",\"material\":\"" + material + "\",\"sound\":\"" + sound + "\",\"wingsState\":\"" + wingsState + "\"}")
                .extract(jsonPath().expression("$.id", "duckId"))
        );

        return "${duckId}";
    }

    public static String createRubberDuck(TestCaseRunner runner) {
        return createDuck(runner, "red", 0.121, "rubber", "quack", "ACTIVE");
    }

    public static String createWoodDuck(TestCaseRunner runner) {
        return createDuck(runner, "brown", 0.2, "wood", "quack", "FIXED");
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



}