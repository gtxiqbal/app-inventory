package id.co.gtx.services;

import id.co.gtx.entity.Sewa;
import id.co.gtx.entity.dto.DtoResponse;

import java.util.List;

public interface SewaService {
    DtoResponse findAll();

    DtoResponse findByNik(Long nik);

    DtoResponse findKaryawanByLaptopId(String id);

    DtoResponse findLaptopByKaryawanNik(Long nik);

    DtoResponse create(List<Sewa> sewas);

    DtoResponse delete(Long nik);

    DtoResponse delete(String id);
}
