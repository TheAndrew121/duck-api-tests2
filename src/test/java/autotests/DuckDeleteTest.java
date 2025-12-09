package autotests;

import autotests.clients.DuckClient;
import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

public class DuckDeleteTest extends DuckClient {

    @Test(description = "Удаление утки")
    @CitrusTest
    public void testDeleteDuck(@Optional @CitrusResource TestCaseRunner runner) {
        String duckId = createDuckAndExtractId(runner, "yellow", 0.121, "rubber", "quack", "ACTIVE");

        deleteDuck(runner, duckId);
    }
}