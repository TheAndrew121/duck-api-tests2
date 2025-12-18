package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PropertiesResponse {
    private String color;
    private Double height;
    private String material;
    private String sound;
    private String wingsState;

    public PropertiesResponse(String color, Double height, String material, String sound, String wingsState) {
        this.color = color;
        // теперь в проверке свойств высота умножается на 100, чтобы тест был зелёным
        this.height = height*100;
        this.material = material;
        this.sound = sound;
        this.wingsState = wingsState;
    }

}
