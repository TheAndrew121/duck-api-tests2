package autotests;

import com.consol.citrus.TestCaseRunner;
import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import io.qameta.allure.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {autotests.config.EndpointConfig.class, autotests.config.ClientConfig.class})
public class BaseTest extends TestNGCitrusSpringSupport {

    // теперь тут только самые базовые методы, в том числе базовые HTTP-запросы

    @Autowired
    public com.consol.citrus.http.client.HttpClient duckService;

    @Autowired
    public org.springframework.jdbc.datasource.SingleConnectionDataSource testDb;


    public static final String CREATE_DUCK = "/api/duck/create";
    public static final String DELETE_DUCK = "/api/duck/delete";
    public static final String FLY_DUCK = "/api/duck/action/fly";
    public static final String SWIM_DUCK = "/api/duck/action/swim";
    public static final String QUACK_DUCK = "/api/duck/action/quack";
    public static final String UPDATE_DUCK = "/api/duck/update";
    public static final String GET_PROPERTIES = "/api/duck/action/properties";


    public void setDuckService(com.consol.citrus.http.client.HttpClient duckService) {
        this.duckService = duckService;
    }

    public void setTestDb(org.springframework.jdbc.datasource.SingleConnectionDataSource testDb) {
        this.testDb = testDb;
    }

    @Step("Извлекаем значение из контекста")
    public String extractFromContext(@CitrusResource TestCaseRunner runner, String variableName) {
        return "${" + variableName + "}";
    }
}