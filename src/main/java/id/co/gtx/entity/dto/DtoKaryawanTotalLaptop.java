package id.co.gtx.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoKaryawanTotalLaptop {
    private Long nik;
    private String firstName;
    private String lastName;
    private Long total;
}
