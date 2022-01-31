package id.co.gtx.services;

import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;

import java.util.List;

public interface LaptopService {
    String generateId();

    DtoResponse findAll();

    DtoResponse findById(String id);

    DtoResponse findByName(String name);

    DtoResponse findByMerk(Merk merk);

    DtoResponse findByMerkAndType(Merk merk, Type type);

    DtoResponse createOrUpdate(Laptop laptop);

    DtoResponse batchUpdate(List<Laptop> laptops);

    DtoResponse delete(String id);
}
