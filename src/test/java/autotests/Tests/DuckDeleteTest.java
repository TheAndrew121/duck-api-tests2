package autotests.Tests;

import autotests.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckDeleteTest extends TestNGCitrusSpringSupport {

    @Test(description = "Удаление утки")
    @CitrusTest
    public void testDeleteDuck(@Optional @CitrusResource TestCaseRunner runner) {

        BaseDuckTest.createDuck(runner, "red", 0.121, "rubber", "quack", "ACTIVE");

        String duckID = BaseDuckTest.validateDuckCreation(runner, "red", 0.121, "rubber", "quack", "ACTIVE");

        BaseDuckTest.deleteDuck(runner, duckID);

        // добавлена проверка на удаление отдельным методом
        BaseDuckTest.validateResponse(runner, "{\"message\":\"Duck is deleted\"}");
    }
}