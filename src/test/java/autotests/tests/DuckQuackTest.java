package autotests.tests;

import autotests.clients.DuckClient;
import autotests.payloads.QuackResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

@Epic("Duck Controller")
@Feature("Quack")
public class DuckQuackTest extends DuckClient {
    private static final int REPETITION_COUNT = 2;
    private static final int SOUND_COUNT = 2;

    @Test(description = "Корректный нечётный id, корректный звук (валидация через ресурс)")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "quack";

        String duckId = createDuckWithParity(this, runner, context, false, quackSound, "yellow", 0.121, "rubber", "ACTIVE");

        quackDuck(runner, duckId, REPETITION_COUNT, SOUND_COUNT);
        validateResponseFromResource(runner, HttpStatus.OK, "DuckQuackTest/quackOddResponse.json");


        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    @Test(description = "Корректный чётный id, корректный звук (валидация через строку)")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "moo";

        String duckId = createDuckWithParity(this, runner, context, true, quackSound, "yellow", 0.121, "rubber", "ACTIVE");

        quackDuck(runner, duckId, REPETITION_COUNT, SOUND_COUNT);

        String expectedSound = buildExpectedSoundMessage(quackSound);
        String expectedResponse = String.format("{\"sound\":\"%s\"}", expectedSound);
        validateResponseFromString(runner, HttpStatus.OK, expectedResponse);

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);;
    }

    @Test(description = "Корректный id, проверка через payload-модель")
    @CitrusTest
    public void quackDuckWithPayloadValidation(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "quack";

        String duckId = createDuckWithParity(this, runner, context, false, quackSound, "yellow", 0.121, "rubber", "ACTIVE");

        quackDuck(runner, duckId, REPETITION_COUNT, SOUND_COUNT);

        String expectedSound = buildExpectedSoundMessage(quackSound);
        QuackResponse expectedResponse = new QuackResponse(expectedSound);
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);

        deleteDuckFromDatabase(runner, duckId);
        validateDuckNotExistsInDatabase(runner, duckId);
    }

    private String generateSingleDuckSound(String duckSound, int repetitionCount) {
        StringBuilder soundBuilder = new StringBuilder();
        for (int i = 0; i < repetitionCount; i++) {
            if (i > 0) {
                soundBuilder.append("-");
            }
            soundBuilder.append(duckSound);
        }
        return soundBuilder.toString();
    }

    private String buildExpectedSoundMessage(String duckSound) {
        StringBuilder expectedMessageBuilder = new StringBuilder();
        String singleSound = generateSingleDuckSound(duckSound, DuckQuackTest.REPETITION_COUNT);

        for (int i = 0; i < DuckQuackTest.SOUND_COUNT; i++) {
            if (i > 0) {
                expectedMessageBuilder.append(", ");
            }
            expectedMessageBuilder.append(singleSound);
        }

        return expectedMessageBuilder.toString();
    }
}