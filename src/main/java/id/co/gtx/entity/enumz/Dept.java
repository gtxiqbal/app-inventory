package id.co.gtx.entity.enumz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Dept {
    BAC("BAC","Bussiness Application Conventional", Divisi.CBS),
    BAS("BAS","Bussiness Application Syariah", Divisi.CBS),
    CBSI("CBSI", "CBS Implementation", Divisi.CBS),
    CBSCS("CBSCS", "CBS Conventional Support", Divisi.CBS),
    CBSSS("CBSSS", "CBS Syariah Support", Divisi.CBS),
    SAD("SAD", "System Application Development", Divisi.CBS),

    FRSS("FRSS", "Financial Reporting Standard System", Divisi.RR),
    RRAI("RRAI", "RRA Implementation", Divisi.RR),

    BIDW("BIDW", "BI & Data Warehouse", Divisi.SS),
    CMSP("CMSP", "CMS & Payment", Divisi.SS),
    SSAI("SSAI", "SSA Implementation", Divisi.SS),
    SWITCHING("SWITCHING", "Switching", Divisi.SS),

    QA("QA", "Quality Assurance", Divisi.QAIS),
    IS("IS", "Information Security", Divisi.QAIS)
    ;

    private final String value;
    private final String desc;
    private final Divisi divis;

    Dept(String value, String desc, Divisi divis) {
        this.value = value;
        this.desc = desc;
        this.divis = divis;
    }
}
