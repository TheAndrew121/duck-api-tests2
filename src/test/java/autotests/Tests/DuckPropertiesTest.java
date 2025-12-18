package autotests.Tests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.PropertiesResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.actions.AbstractTestAction;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

// TODO: SHIFT-AQA-1
public class DuckPropertiesTest extends DuckClient {

    @Test(description = "Получение свойств утки с material = wood и чётным ID")
    @CitrusTest
    public void testGetPropertiesWood(@Optional @CitrusResource TestCaseRunner runner,
                                      @Optional @CitrusResource TestContext context) {

        String duckId = createDuckWithCertainId(runner, context,
                "brown", 0.2, "wood", "quack", "FIXED", true);

        getDuckProperties(runner, duckId);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckPropertiesTest/woodPropertiesResponse.json");

        validateDuckIdEvenness(runner, true);

        deleteDuck(runner, duckId);
        validateResponseWithMessage(this, runner, HttpStatus.OK, "Duck is deleted");
    }

    @Test(description = "Свойства утки с material = rubber и нечётным ID")
    @CitrusTest
    public void testGetPropertiesRubber(@Optional @CitrusResource TestCaseRunner runner,
                                        @Optional @CitrusResource TestContext context) {

        String duckId = createDuckWithCertainId(runner, context,
                "red", 50, "rubber", "quack", "FIXED", false);

        getDuckProperties(runner, duckId);

        PropertiesResponse expected = new PropertiesResponse("red", 5000.0, "rubber", "quack", "FIXED");
        validateResponseFromPayload(runner, HttpStatus.OK, expected);

        validateDuckIdEvenness(runner, false);

        deleteDuck(runner, duckId);
        validateResponseWithMessage(this, runner, HttpStatus.OK, "Duck is deleted");
    }


    private String createDuckWithCertainId(TestCaseRunner runner, TestContext context,
                                           String color, double height, String material,
                                           String sound, String wingsState, boolean shouldBeEven) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            CreateRequest req = new CreateRequest(color, height, material, sound, wingsState);
            createDuck(runner, req);

            extractIdFromResponse(this, runner);

            runner.run(new AbstractTestAction() {
                @Override
                public void doExecute(TestContext ctx) {
                    String duckId = ctx.getVariable("duckId");
                    long id = Long.parseLong(duckId);
                    ctx.setVariable("isIdEven", id % 2 == 0);
                    ctx.setVariable("currentDuckId", duckId);
                }
            });

            boolean isEven = Boolean.parseBoolean(context.getVariable("isIdEven"));
            String currentDuckId = context.getVariable("currentDuckId");

            if (isEven == shouldBeEven) {
                System.out.println("Создана утка (" + material + ") с " +
                        (shouldBeEven ? "чётным" : "нечётным") +
                        " ID: " + currentDuckId + " (попытка: " + attempt + ")");
                return currentDuckId;
            } else {
                System.out.println("Утка (" + material + ") создана с " +
                        (isEven ? "чётным" : "нечётным") +
                        " ID: " + currentDuckId + " - не подходит, удаляем");
                deleteDuck(runner, currentDuckId);
            }
        }

        throw new RuntimeException("Не удалось создать утку (" + material +
                ") с " + (shouldBeEven ? "чётным" : "нечётным") + " ID после 2 попыток");
    }
}