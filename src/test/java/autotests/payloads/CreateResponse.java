package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateResponse {

    // id в ответе пока что будет String, так как значение id в ответе сейчас игнорируется. Потом исправлю
    private String id;
    private String color;
    private double height;
    private String material;
    private String sound;
    private String wingsState;

    public CreateResponse(String id, String color, double height, String material, String sound, String wingsState) {
        this.id = id;
        this.color = color;
        this.height = height;
        this.material = material;
        this.sound = sound;
        this.wingsState = wingsState;
    }

}
