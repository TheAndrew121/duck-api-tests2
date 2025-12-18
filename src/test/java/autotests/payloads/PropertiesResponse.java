package autotests.payloads;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель для ответа /api/duck/action/properties
 * Может быть пустой ({}) или содержать поля — потому поля nullable/defaults.
 */
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
        this.height = height;
        this.material = material;
        this.sound = sound;
        this.wingsState = wingsState;
    }

}
