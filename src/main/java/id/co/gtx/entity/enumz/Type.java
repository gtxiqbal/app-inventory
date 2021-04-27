package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Type {
    E5476G("E5476G", Merk.ACER),
    E5477G("E5477G", Merk.ACER),
    ROG("ROG", Merk.ASUS),
    ZENBOOK("ZENBOOK", Merk.ASUS),
    XPS("XPS", Merk.DELL),
    LATITUDE("LATITUDE", Merk.DELL),
    ELITBOOK("ELITBOOK", Merk.HP),
    PAVILION("PAVILION", Merk.HP),
    LEGION("LEGION", Merk.LENOVO),
    IDEAPAD("IDEAPAD", Merk.LENOVO);

    private final String value;
    private final Merk merk;

    Type(String value, Merk merk) {
        this.value = value;
        this.merk = merk;
    }
}
