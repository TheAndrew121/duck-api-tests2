package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.context.TestContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import static com.consol.citrus.dsl.JsonPathSupport.jsonPath;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;


// этот тест заработал после того, как посоветовался с командой

// TODO: SHIFT-AQA-3
public class DuckQuackTest extends DuckClient {
    private static final int REPETITION_COUNT = 2;
    private static final int SOUND_COUNT = 2;

    @Test(description = "Корректный нечётный id, корректный звук")
    @CitrusTest
    public void quackDuckWithOddId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "quack";
        String[] duckData = createDuckWithIdParity(runner, context, false, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);
        validateQuackResponse(runner, duckData[1], REPETITION_COUNT, SOUND_COUNT);
        deleteDuck(runner, duckData[0]);
    }

    @Test(description = "Корректный чётный id, корректный звук")
    @CitrusTest
    public void quackDuckWithEvenId(@Optional @CitrusResource TestCaseRunner runner, @Optional @CitrusResource TestContext context) {
        String quackSound = "moo";
        String[] duckData = createDuckWithIdParity(runner, context, true, quackSound);
        quackDuck(runner, duckData[0], REPETITION_COUNT, SOUND_COUNT);
        validateQuackResponse(runner, duckData[1], REPETITION_COUNT, SOUND_COUNT);
        deleteDuck(runner, duckData[0]);
    }

    private String[] createDuckWithIdParity(@CitrusResource TestCaseRunner runner, @CitrusResource TestContext context, boolean shouldBeEven, String quackSound)
    {
        String duckId;
        boolean duckCreatedWithDesiredParity;

        do {
            // создаём утку и получаем ID как переменную Citrus
            createDuck(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

            // получаем ID из ответа
            runner.$(http()
                    .client(duckService)
                    .receive()
                    .response(HttpStatus.OK)
                    .message()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .extract(jsonPath().expression("$.id", "currentDuckId"))
            );

            // фактическое значение ID из контекста
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

    private void validateQuackResponse(@CitrusResource TestCaseRunner runner, String duckSound, int repetitionCount, int soundCount) {
        String expectedSound = buildExpectedSoundMessage(duckSound, repetitionCount, soundCount);
        String expectedResponse = String.format("{\"sound\":\"%s\"}", expectedSound);

        runner.$(http()
                .client(duckService)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(expectedResponse)
        );
    }
}