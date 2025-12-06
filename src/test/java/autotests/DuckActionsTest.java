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

public class DuckActionsTest extends TestNGCitrusSpringSupport {

    @Test(description = "Самый простой тест - создание утки")
    @CitrusTest
    public void simplestTest(@Optional @CitrusResource TestCaseRunner runner) {
        // Шаг 1: Создаем утку
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"yellow\",\"height\":0.03,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );

        // Шаг 2: Ждем успешный ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        runner.$(echo("Тест пройден! Утка создана."));
    }

    @Test(description = "Тест кряканья утки")
    @CitrusTest
    public void quackTest(@Optional @CitrusResource TestCaseRunner runner) {
        // Запрашиваем кряканье утки с ID=1
        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/quack")
                .queryParam("id", "1")
                .queryParam("repetitionCount", "2")
                .queryParam("soundCount", "3")
        );

        // Проверяем ответ
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"sound\":\"quack-quack, quack-quack, quack-quack\"}")
        );

        runner.$(echo("Утка крякает!"));
    }
}