package autotests.Tests;

import autotests.BaseDuckTest;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckFlyTest extends TestNGCitrusSpringSupport {

    @Test(description = "Существующий id с ACTIVE крыльями")
    @CitrusTest
    public void testFlyWithActiveWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = BaseDuckTest.createRubberDuck(runner);

        BaseDuckTest.flyDuck(runner, duckId);

        BaseDuckTest.validateResponseWithMessage(runner, HttpStatus.OK, "I am flying :)");

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Существующий id со FIXED крыльями")
    @CitrusTest
    public void testFlyWithFixedWings(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = BaseDuckTest.createWoodDuck(runner);

        BaseDuckTest.flyDuck(runner, duckId);

        BaseDuckTest.validateResponseWithMessage(runner, HttpStatus.OK, "I can not fly :C");

        BaseDuckTest.deleteDuck(runner, duckId);
    }

    @Test(description = "Существующий id с UNDEFINED крыльями")
    @CitrusTest
    public void testFlyWithUndefinedWings(@Optional @CitrusResource TestCaseRunner runner) {
        // Создаем утку с UNDEFINED крыльями
        String duckId = BaseDuckTest.createDuck(runner, "yellow", 0.121, "rubber", "quack", "UNDEFINED");

        // Тестируем полет
        BaseDuckTest.flyDuck(runner, duckId);

        // Проверяем ответ
        BaseDuckTest.validateResponseWithMessage(runner, HttpStatus.OK, "Wings are not detected :(");

        // Удаляем утку после теста
        BaseDuckTest.deleteDuck(runner, duckId);
    }
}