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

public class DuckFlyTest extends TestNGCitrusSpringSupport {

    @Test(description = "Существующий id с ACTIVE крыльями")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", "14")
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"I am flying :)\"}")
        );
    }

    @Test(description = "Существующий id со FIXED крыльями")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", "13")
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"I can not fly :C\"}")
        );
    }

    @Test(description = "Существующий id с UNDEFINED крыльями")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {

        runner.$(http()
                .client("http://localhost:2222")
                .send()
                .get("/api/duck/action/fly")
                .queryParam("id", "2")
        );

        runner.$(http()
                .client("http://localhost:2222")
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body("{\"message\":\"Wings are not detected :(\"}")
        );
    }

}