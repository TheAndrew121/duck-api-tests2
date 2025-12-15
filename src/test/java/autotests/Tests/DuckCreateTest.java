package autotests.Tests;

import autotests.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckCreateTest extends TestNGCitrusSpringSupport {

    @Test(description = "Создать утку с material = rubber")
    @CitrusTest
    public void testCreateRubberDuck(@Optional @CitrusResource TestCaseRunner runner) {

        // в 28 домашке валидация создания ещё в самом методе создания утки в BaseTest
        String duckID = BaseDuckTest.createDuck(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        // проверка на чётность
        BaseDuckTest.isDuckIdEven(runner);

        BaseDuckTest.deleteDuck(runner, duckID);
    }

    @Test(description = "Создать утку с material = wood")
    @CitrusTest
    public void testCreateWoodDuck(@Optional @CitrusResource TestCaseRunner runner) {


        String duckID = BaseDuckTest.createDuck(runner, "brown", 0.2, "wood", "quack", "FIXED");

        // проверка на чётность
        BaseDuckTest.isDuckIdEven(runner);

        BaseDuckTest.deleteDuck(runner, duckID);

    }
}