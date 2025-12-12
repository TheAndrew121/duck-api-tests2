package autotests;

import autotests.clients.DuckClient;
import autotests.payloads.CreateRequest;
import autotests.payloads.QuackResponse;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

// TODO: SHIFT-AQA-2
public class DuckQuackTest extends DuckClient {
    private static final int REPETITION_COUNT = 2;
    private static final int SOUND_COUNT = 2;

    @Test(description = "Корректный нечётный id, корректный звук (валидация через ресурс)")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "quack";
        String[] duckData = createDuckWithIdParity(runner, context, false, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);

        validateResponseFromResource(runner, HttpStatus.OK, "DuckQuackTest/quackOddResponse.json");

        deleteDuck(runner, duckData[0]);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Корректный чётный id, корректный звук (валидация через строку)")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "moo";
        String[] duckData = createDuckWithIdParity(runner, context, true, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);

        String expectedSound = buildExpectedSoundMessage(quackSound, REPETITION_COUNT, SOUND_COUNT);
        String expectedResponse = String.format("{\"sound\":\"%s\"}", expectedSound);
        validateResponseFromString(runner, HttpStatus.OK, expectedResponse);

        deleteDuck(runner, duckData[0]);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    @Test(description = "Корректный id, проверка через payload-модель")
    @CitrusTest
    public void quackDuckWithPayloadValidation(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "quack";
        String[] duckData = createDuckWithIdParity(runner, context, false, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);

        // валидация через payload-модель
        String expectedSound = buildExpectedSoundMessage(quackSound, REPETITION_COUNT, SOUND_COUNT);
        QuackResponse expectedResponse = new QuackResponse(expectedSound);
        validateResponseFromPayload(runner, HttpStatus.OK, expectedResponse);

        deleteDuck(runner, duckData[0]);
        validateResponseFromString(runner, HttpStatus.OK, "{\"message\":\"Duck is deleted\"}");
    }

    private String[] createDuckWithIdParity(@CitrusResource TestCaseRunner runner, @CitrusResource TestContext context,
                                            boolean shouldBeEven, String quackSound) {
        String duckId;
        boolean duckCreatedWithDesiredParity;

        do {
            CreateRequest req = new CreateRequest("yellow", 0.121, "rubber", quackSound, "ACTIVE");
            createDuck(runner, req);

            runner.$(http()
                    .client(duckService)
                    .receive()
                    .response(HttpStatus.OK)
                    .message()
                    .extract(jsonPath().expression("$.id", "currentDuckId"))
            );

            duckId = context.getVariable("currentDuckId");
            duckCreatedWithDesiredParity = isEvenId(duckId) == shouldBeEven;

        } while (!duckCreatedWithDesiredParity);

        return new String[]{duckId, quackSound};
    }

    private boolean isEvenId(String id) {
        return Integer.parseInt(id) % 2 == 0;
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

    private String buildExpectedSoundMessage(String duckSound, int repetitionCount, int soundCount) {
        StringBuilder expectedMessageBuilder = new StringBuilder();
        String singleSound = generateSingleDuckSound(duckSound, repetitionCount);

        for (int i = 0; i < soundCount; i++) {
            if (i > 0) {
                expectedMessageBuilder.append(", ");
            }
            expectedMessageBuilder.append(singleSound);
        }

        return expectedMessageBuilder.toString();
    }
}