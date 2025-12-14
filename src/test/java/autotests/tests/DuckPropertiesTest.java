package autotests.tests;

import autotests.clients.DuckClient;
import autotests.payloads.PropertiesResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

// TODO: SHIFT-AQA-1

@Epic("Duck Controller")
@Feature("Properties")
public class DuckPropertiesTest extends DuckClient {

    @Test(description = "Получение свойств утки с material = wood (валидация через resource)")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {

        String duckId = createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

        getDuckProperties(runner, duckId);
        validateResponseFromResource(runner, HttpStatus.OK, "DuckPropertiesTest/woodPropertiesResponse.json");

        // дополнительная проверка на чётность или нечётность id (результат в логах теста)
        validateDuckIdParity(this, runner, context, duckId);

        validateDuckInDatabase(runner, duckId, "brown", "0.2", "wood", "quack", "FIXED");

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Свойства утки с material = rubber (валидация через payload-модель)")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {

        String duckId = createDuckInDatabase(runner, "red", 50.0, "rubber", "quack", "FIXED");

        getDuckProperties(runner, duckId);
        PropertiesResponse expected = new PropertiesResponse("red", 50.0, "rubber", "quack", "FIXED");
        validateResponseFromPayload(runner, HttpStatus.OK, expected);

        // дополнительная проверка на чётность или нечётность id (результат в логах теста)
        validateDuckIdParity(this, runner, context, duckId);

        validateDuckInDatabase(runner, duckId, "red", "50.0", "rubber", "quack", "FIXED");

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

}