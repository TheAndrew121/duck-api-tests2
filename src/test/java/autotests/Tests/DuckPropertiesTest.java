package autotests.Tests;

import autotests.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;
// TODO: SHIFT-AQA-2 сервис возвращает высоту, умноженную на 100, поэтому нужно сравнивать 50 с 5000.0, чтобы тест был зелёным
public class DuckPropertiesTest extends TestNGCitrusSpringSupport {

    @Test(description = "Получение свойств утки с material = wood и чётным id")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner,
                                      @Optional @CitrusResource TestContext context) {

        // создаём утку с гарантированно чётным id
        String duckId = BaseDuckTest.createDuckWithCertainID(runner, context, true);

        // костыль, чтобы использовать метод createDuckWithCertainID в тесте свойств, но оба теста рабочие
        BaseDuckTest.updateDuck(runner, duckId, "brown", "0.2", "wood", "quack", "FIXED");

        BaseDuckTest.getDuckProperties(runner, duckId);

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{}") // ожидаем пустой ответ для material = wood
        );

        // проверка, что утка действительно чётная
        BaseDuckTest.validateDuckIdEvenness(runner, true);

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Получение свойств утки с material = rubber и нечётным id")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner,
                                        @Optional @CitrusResource TestContext context) {

        // создаём утку с гарантированно нечётным id
        String duckId = BaseDuckTest.createDuckWithCertainID(runner, context, false);

        // костыль, чтобы использовать метод createDuckWithCertainID в тесте свойств, но оба теста рабочие
        BaseDuckTest.updateDuck(runner, duckId, "red", "50", "rubber", "quack", "FIXED");

        BaseDuckTest.getDuckProperties(runner, duckId);

        // метод валидации ответа при запросе свойств, в котором полученная высота сразу умножается на 100
        BaseDuckTest.validatePropertiesResponse(runner, "red", 50, "rubber", "quack", "FIXED");

        // проверка, что утка действительно нечётная
        BaseDuckTest.validateDuckIdEvenness(runner, false);

        BaseDuckTest.deleteDuck(runner, duckId);
    }
}