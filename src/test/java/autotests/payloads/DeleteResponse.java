package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteResponse {
    private String message;

    public DeleteResponse(String message) {
        this.message = message;
    }
}