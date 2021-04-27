package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Status {
    OUTSOURCING(0, "Outsourcing"),
    TETAP(1, "Tetap"),
    KONTRAK(2, "Kontrak"),
    ;
    private final Integer value;
    private final String desc;

    Status(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
