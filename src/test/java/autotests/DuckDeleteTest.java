package autotests;

import autotests.common.BaseDuckTest;
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

        String duckId = BaseDuckTest.createRubberDuck(runner);

        BaseDuckTest.deleteDuck(runner, duckId);
    }
}