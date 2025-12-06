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

public class DuckCreateTest extends TestNGCitrusSpringSupport {

    @Test(description = "Создать утку с material = rubber")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"yellow\",\"height\":0.121,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"ACTIVE\"}")
        );

        // Проверяем ответ, игнорируя поле id (оно генерируется сервером)
        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"@ignore@\",\"color\":\"yellow\",\"height\":0.121,\"material\":\"rubber\",\"sound\":\"quack\",\"wingsState\":\"ACTIVE\"}")
        );
    }

    @Test(description = "Создать утку с material = wood")
    @CitrusTest
    public void testCreateWoodDuck(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .post("/api/duck/create")
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"color\":\"brown\",\"height\":0.2,\"material\":\"wood\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"id\":\"@ignore@\",\"color\":\"brown\",\"height\":0.2,\"material\":\"wood\",\"sound\":\"quack\",\"wingsState\":\"FIXED\"}")
        );
    }
}