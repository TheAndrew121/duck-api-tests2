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

    @Test(description = "Получение свойств wood утки с чётным ID (валидация через resource)")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner,
                                      @Optional @CitrusResource TestContext context) {
        String duckId = createWoodDuckWithEvenId(runner, context);

        getDuckProperties(runner, duckId);
        validateResponseFromResource(runner, HttpStatus.OK, "DuckPropertiesTest/woodPropertiesResponse.json");

        validateDuckIdParityPR(runner, context, duckId, true);

        validateDuckInDatabase(runner, duckId, "brown", "0.2", "wood", "quack", "FIXED");

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Свойства rubber утки с нечётным ID (валидация через payload-модель)")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner,
                                        @Optional @CitrusResource TestContext context) {

        String duckId = createRubberDuckWithOddId(runner, context);

        getDuckProperties(runner, duckId);

        PropertiesResponse expected = new PropertiesResponse("red", 50.0, "rubber", "quack", "FIXED");
        validateResponseFromPayload(runner, HttpStatus.OK, expected);

        validateDuckIdParityPR(runner, context, duckId, false);

        validateDuckInDatabase(runner, duckId, "red", "50.0", "rubber", "quack", "FIXED");

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }


    private String createWoodDuckWithEvenId(TestCaseRunner runner, TestContext context) {
        for (int attempt = 1; attempt <= 2; attempt++) {

            String duckIdTemplate = createDuckInDatabase(runner, "brown", 0.2, "wood", "quack", "FIXED");

            String actualDuckId = getActualIdFromTemplate(runner, context, duckIdTemplate);

            long id = Long.parseLong(actualDuckId);
            boolean isEven = id % 2 == 0;

            if (isEven) {
                System.out.println("Создана wood утка с чётным ID: " + actualDuckId + " (попытка: " + attempt + ")");
                return actualDuckId;
            } else {
                System.out.println("Wood утка создана с нечётным ID: " + actualDuckId + " - удаляем и пробуем снова");
            }
        }

        throw new RuntimeException("Не удалось создать wood утку с чётным ID после 2 попыток");
    }

    private String createRubberDuckWithOddId(TestCaseRunner runner, TestContext context) {
        for (int attempt = 1; attempt <= 2; attempt++) {

            String duckIdTemplate = createDuckInDatabase(runner, "red", 50.0, "rubber", "quack", "FIXED");

            String actualDuckId = getActualIdFromTemplate(runner, context, duckIdTemplate);

            long id = Long.parseLong(actualDuckId);
            boolean isEven = id % 2 == 0;

            if (!isEven) {
                System.out.println("Создана rubber утка с нечётным ID: " + actualDuckId + " (попытка: " + attempt + ")");
                return actualDuckId;
            } else {
                System.out.println("Rubber утка создана с чётным ID: " + actualDuckId + " - удаляем и пробуем снова");
                deleteDuckFromDatabase(runner, actualDuckId);
            }
        }

        throw new RuntimeException("Не удалось создать rubber утку с нечётным ID после 2 попыток");
    }


}