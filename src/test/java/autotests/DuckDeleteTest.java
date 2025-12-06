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

public class DuckDeleteTest extends TestNGCitrusSpringSupport {

    @Test(description = "Удалить утку")
    @CitrusTest
    public void testDeleteDuck(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .delete("/api/duck/delete")
                .queryParam("id", "6")
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"Duck is deleted\"}")
        );
    }
}