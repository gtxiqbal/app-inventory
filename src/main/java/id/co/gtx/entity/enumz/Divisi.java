package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Divisi {
    CBS("CBS", "Core Banking System"),
    RR("RR", "Regulatory Report"),
    SS("SS", "Switching & Surrounding Application"),
    QAIS("QAIS", "Quality Assurance & Information Security")
    ;
    private final String value;
    private final String desc;

    Divisi(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
