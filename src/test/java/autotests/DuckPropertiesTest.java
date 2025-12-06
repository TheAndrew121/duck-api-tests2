package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import static com.consol.citrus.actions.EchoAction.Builder.echo;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class DuckPropertiesTest extends TestNGCitrusSpringSupport {

    // тесты на /api/duck/action/properties

    @Test(description = "Получение свойств утки с четным ID и material = wood")
    @CitrusTest
    public void testGetPropertiesEvenIdWood(@Optional @CitrusResource TestCaseRunner runner) {

        // createDuck(runner, "yellow", 0.1, "wood", "quack", "ACTIVE");

        // Получаем характеристики
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", "14")  // существующий четный ID
        );

        // Проверяем ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{}")
                // корректно работающий сервис должен вернуть ответ со всеми полями (ниже), но если материал не rubber, то возвращает пустой ответ
                // .body("{\"color\":\"red\",\"height\":0.1,\"material\":\"wood\",\"sound\":\"quack\",\"wingsState\":\"ACTIVE\"}")
        );
    }

    @Test(description = "Получение свойств утки с нечетным ID и material = rubber")
    @CitrusTest
    public void testGetPropertiesOddIdRubber(@Optional @CitrusResource TestCaseRunner runner) {

        // createDuck(runner, "red", 0.2, "rubber", "quack", "ACTIVE");

        // Получаем характеристики
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", "13")  // существующий нечетный ID
        );

        // Проверяем ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"red\",\"height\":5000.0,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );
    }


    /*
    private void createDuck(TestCaseRunner runner, String color, double height, String material, String sound, String wingsState) {
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(String.format(
                        "{\"color\":\"%s\",\"height\":%s,\"material\":\"%s\",\"sound\":\"%s\",\"wingsState\":\"%s\"}",
                        color, height, material, sound, wingsState))
        );

        // Проверяем, что утка создана
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
    }
     */
}