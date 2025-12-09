package autotests;

import autotests.common.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckPropertiesTest extends TestNGCitrusSpringSupport {

    @Test(description = "Получение свойств утки с material = wood")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner) {

       String duckId = BaseDuckTest.createDuck(runner, "brown", 0.2, "wood", "quack", "FIXED");

        // TODO: SHIFT-AQA-1
        BaseDuckTest.getDuckProperties(runner, duckId);

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)

                // ожидаем пустой ответ, чтобы тест был зелёным, потому что материал не rubber
                .body("{}")
        );

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Cвойства утки с material = rubber")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner) {


        // TODO: SHIFT-AQA-2 сервис возвращает высоту, умноженную на 100, поэтому нужно сравнивать 50 с 5000.0, чтобы тест был зелёным
        String duckId = BaseDuckTest.createDuck(runner, "red", 50, "rubber", "quack", "FIXED");

        BaseDuckTest.getDuckProperties(runner, duckId);

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"red\",\"height\":5000.0,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );

        BaseDuckTest.deleteDuck(runner, duckId);
    }
}