package autotests.clients;

import autotests.common.BaseTest;
import autotests.payloads.CreateRequest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckClient extends BaseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return extractIdFromResponse(runner);
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
        return extractIdFromResponse(runner);
    }
}
