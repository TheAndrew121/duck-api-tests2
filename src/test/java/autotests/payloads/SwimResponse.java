package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SwimResponse {
    private String message;

    public SwimResponse(String message) {
        this.message = message;
    }
}