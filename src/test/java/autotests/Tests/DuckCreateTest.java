package autotests.Tests;

import autotests.BaseDuckTest;
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

        BaseDuckTest.createDuck(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        String duckID = BaseDuckTest.validateDuckCreation(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        BaseDuckTest.deleteDuck(runner, duckID);

    }

    @Test(description = "Создать утку с material = wood")
    @CitrusTest
    public void testCreateWoodDuck(@Optional @CitrusResource TestCaseRunner runner) {

        BaseDuckTest.createDuck(runner, "brown", 0.2, "wood", "quack", "FIXED");

        String duckID = BaseDuckTest.validateDuckCreation(runner, "brown", 0.2, "wood", "quack", "FIXED");

        BaseDuckTest.deleteDuck(runner, duckID);

    }
}