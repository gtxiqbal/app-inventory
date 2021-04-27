package id.co.gtx.services;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.dto.DtoResponse;

public interface KaryawanService {
    Long generateNik();

    Long generateNikOutsouring();

    DtoResponse findAll();

    DtoResponse findByNik(Long nik);

    DtoResponse findByFirstName(String firstName);

    DtoResponse findByLastName(String lastName);

    DtoResponse findByEmail(String email);

    DtoResponse createOrUpdate(Karyawan karyawan, boolean isUpdate, Long nikOld);

    DtoResponse delete(Long nik);
}
