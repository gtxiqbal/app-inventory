package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Merk {
    ACER("ACER", "ACER"),
    ASUS("ASUS", "ASUS"),
    DELL("DELL", "DELL"),
    HP("HP", "Hewlett-Packard"),
    LENOVO("LENOVO", "LENOVO");

    private final String value;
    private final String desc;

    Merk(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
