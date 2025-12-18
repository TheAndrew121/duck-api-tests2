package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuackResponse {
    private String sound;

    public QuackResponse(String sound) {
        this.sound = sound;
    }
}