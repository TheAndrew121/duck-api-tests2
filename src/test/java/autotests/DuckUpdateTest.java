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

public class DuckUpdateTest extends TestNGCitrusSpringSupport {

    @Test(description = "Изменить цвет и высоту уточки")
    @CitrusTest
    public void testUpdateColorAndHeight(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .put("/api/duck/update")
                .queryParam("id", "13")
                .queryParam("color", "BLACK")      // изменяем цвет
                .queryParam("height", "0.555")     // изменяем высоту
                .queryParam("material", "rubber")  // текущий материал (не меняем)
                .queryParam("sound", "quack")      // текущий звук (не меняем)
                .queryParam("wingsState", "FIXED") // текущее состояние крыльев (не меняем)
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"Duck with id = 13 is updated\"}")
        );
    }

    @Test(description = "Изменить цвет и звук уточки")
    @CitrusTest
    public void testUpdateColorAndSound(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .put("/api/duck/update")
                .queryParam("id", "14")
                .queryParam("color", "blue")       // изменяем цвет
                .queryParam("height", "0.1")       // текущая высота (не меняем)
                .queryParam("material", "rubber")    // текущий материал (не меняем)
                .queryParam("sound", "quack-quack!") // изменяем звук
                .queryParam("wingsState", "ACTIVE") // текущее состояние крыльев (не меняем)
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"Duck with id = 14 is updated\"}")
        );
    }
}