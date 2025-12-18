package autotests.config;

import autotests.clients.DuckClient;
import autotests.clients.DuckClientDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    // spring-конфигурация клиентов

    @Autowired
    private com.consol.citrus.http.client.HttpClient duckService;

    @Autowired
    private org.springframework.jdbc.datasource.SingleConnectionDataSource testDb;

    @Bean
    public DuckClient duckClient() {
        DuckClient client = new DuckClient();
        client.setDuckService(duckService);
        client.setTestDb(testDb);
        return client;
    }

    @Bean
    public DuckClientDB duckClientDB() {
        DuckClientDB client = new DuckClientDB();
        client.setDuckService(duckService);
        client.setTestDb(testDb);
        return client;
    }
}