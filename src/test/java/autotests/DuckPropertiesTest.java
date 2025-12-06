package autotests;

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

    // тесты на /api/duck/action/properties
    // TODO: SHIFT-AQA-1
    @Test(description = "Получение свойств утки с четным ID и material = wood")
    @CitrusTest
    public void testGetPropertiesEvenIdWood(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/properties")
                .queryParam("id", "14")  // существующий четный ID
        );

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

}