package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlyResponse {
    private String message;

    public FlyResponse(String message) {
        this.message = message;
    }
}