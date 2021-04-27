package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Jabatan {
    KADIV("KADIV", "Kepala Divisi"),
    KADEPT("KADEPT", "Kepala Departement"),
    ASMAN("ASMAN", "Assisten Manager"),

    PROGRAMMER("PROGRAMMER", "Staff Programmer"),
    QA("QA", "Quality Assurance"),
    SA("SA", "System Analayst"),
    ;

    private final String value;
    private final String desc;

    Jabatan(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
