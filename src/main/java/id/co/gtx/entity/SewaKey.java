package id.co.gtx.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class SewaKey implements Serializable {
    private static final long serialVersionUID = -6330550675962091557L;

    private Long karyawanNik;
    private String laptopId;
}
