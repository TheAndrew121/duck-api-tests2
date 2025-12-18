package autotests.payloads;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(fluent = true)
public class CreateRequest {
    private String color;
    private double height;
    private String material;
    private String sound;
    private String wingsState;

    public CreateRequest(String color, double height, String material, String sound, String wingsState) {
        this.color = color;
        this.height = height;
        this.material = material;
        this.sound = sound;
        this.wingsState = wingsState;
    }

}
