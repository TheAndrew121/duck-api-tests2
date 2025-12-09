package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import autotests.common.BaseDuckTest;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckUpdateTest extends TestNGCitrusSpringSupport {

    @Test(description = "Изменить цвет и высоту уточки")
    @CitrusTest
    public void testUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = BaseDuckTest.createDuck(runner, "yellow", 0.121, "rubber", "quack", "FIXED");

        BaseDuckTest.updateDuck(runner, duckId, "BLACK", "0.555", "rubber", "quack", "FIXED");

        // Проверяем ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format("{\"message\":\"Duck with id = %s is updated\"}", duckId))
        );

        // Удаляем утку после теста
        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Изменить цвет и звук уточки")
    @CitrusTest
    public void testUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {
        // Создаем утку для теста
        String duckId = BaseDuckTest.createRubberDuck(runner);

        // Обновляем утку
        BaseDuckTest.updateDuck(runner, duckId, "blue", "0.1", "rubber", "quack-quack!", "ACTIVE");

        // Проверяем ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format("{\"message\":\"Duck with id = %s is updated\"}", duckId))
        );

        // Удаляем утку после теста
        BaseDuckTest.deleteDuck(runner, duckId);
    }
}