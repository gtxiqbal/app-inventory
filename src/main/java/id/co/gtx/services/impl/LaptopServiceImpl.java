package id.co.gtx.services.impl;

import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;
import id.co.gtx.repository.LaptopRepository;
import id.co.gtx.services.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("laptopService")
public class LaptopServiceImpl implements LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    @Override
    public String generateId() {
        String id = "0000000000" + laptopRepository.generateId();
        id = id.substring(id.length() - 10);
        return id;
    }

    @Override
    public DtoResponse findAll() {
        DtoResponse response = new DtoResponse("GET ALL LAPTOP", "FAILED", "Data Laptop Kosong");
        List<Laptop> laptops = laptopRepository.findAll();
        if (laptops.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Semua Daftar Laptop");
            response.setData(laptops);
        }
        return response;
    }

    @Override
    public DtoResponse findById(String id) {
        DtoResponse response = new DtoResponse("GET LAPTOP", "FAILED", "Laptop ID Tidak Ditemukan");
        Optional<Laptop> laptop = laptopRepository.findById(id);
        if (laptop.isPresent()) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Daftar Laptop BY ID");
            response.setData(laptop.get());
        }
        return response;
    }

    @Override
    public DtoResponse findByName(String name) {
        DtoResponse response = new DtoResponse("GET LAPTOP", "FAILED", "Nama Laptop Tidak Ditemukan");
        List<Laptop> laptops = laptopRepository.findByNameLike("%" + name + "%");
        if (laptops.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil, Nama Laptop Ditemukan");
            response.setData(laptops);
        }
        return response;
    }

    @Override
    public DtoResponse findByMerk(Merk merk) {
        DtoResponse response = new DtoResponse("GET LAPTOP", "FAILED", "Merk Laptop Tidak Ditemukan");
        List<Laptop> laptops = laptopRepository.findByMerk(merk);
        if (laptops.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil, Merk Laptop Ditemukan");
            response.setData(laptops);
        }
        return response;
    }

    @Override
    public DtoResponse findByMerkAndType(Merk merk, Type type) {
        DtoResponse response = new DtoResponse("GET LAPTOP", "FAILED", "Merk Dan Tipe Laptop Tidak Ditemukan");
        List<Laptop> laptops = laptopRepository.findByMerkAndType(merk, type);
        if (laptops.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil, Merk Dan Tipe Laptop Ditemukan");
            response.setData(laptops);
        }
        return response;
    }

    @Override
    @Transactional
    public DtoResponse createOrUpdate(Laptop laptop) {
        DtoResponse response = findById(laptop.getId());
        if (response.getStatus().equals("SUCCESS")) {
            return update(laptop);
        } else {
            return create(laptop);
        }
    }

    @Override
    @Transactional
    public DtoResponse batchUpdate(List<Laptop> laptops) {
        List<String> idLaptop = new ArrayList<>();
        for (Laptop laptop: laptops) {
            if (!"".equals(laptop.getId()) && laptop.getId() != null) {
                idLaptop.add(laptop.getId());
            }
        }
        List<Laptop> laptopList = laptopRepository.findAllById(idLaptop);
        return update(laptopList);
    }

    @Override
    @Transactional
    public DtoResponse delete(String id) {
        DtoResponse response = new DtoResponse("DELETE LAPTOP", "FAILED", "Laptop ID Tidak Ditemukan");
        DtoResponse<Laptop> laptopResp = findById(id);
        if (laptopResp.getStatus().equals("SUCCESS")) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Hapus Data Laptop");
            response.setData(laptopResp.getData());
            laptopRepository.delete(laptopResp.getData());
        }
        return response;
    }

    private DtoResponse create(Laptop laptop) {
        DtoResponse response = new DtoResponse("SAVE LAPTOP", "FAILED", "Gagal Menyimpan Data Laptop");
        laptop.setCreateAt(new Date(new Date().getTime()));
        laptop = laptopRepository.save(laptop);
        if (laptop != null) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menambahkan Data Laptop");
            response.setData(laptop);
        }
        return response;
    }

    private DtoResponse update(Laptop laptop) {
        DtoResponse response = new DtoResponse("UPDATE LAPTOP", "FAILED", "Gagal Update Data Laptop");
        DtoResponse laptopResp = findById(laptop.getId());
        if (laptopResp.getStatus().equals("SUCCESS")) {
            Laptop newLaptop = (Laptop) laptopResp.getData();
            newLaptop.setName(laptop.getName());
            newLaptop.setMerk(laptop.getMerk());
            newLaptop.setType(laptop.getType());
            newLaptop.setCpu(laptop.getCpu());
            newLaptop.setRam(laptop.getRam());
            newLaptop.setVga(laptop.getVga());
            newLaptop.setRelease(laptop.getRelease());
            newLaptop.setUpdateAt(new Date(new Date().getTime()));
            newLaptop = laptopRepository.save(laptop);
            if (newLaptop != null) {
                response.setStatus("SUCCESS");
                response.setMsg("Berhasil Update Data Laptop");
                response.setData(newLaptop);
            }
        } else {
            response.setMsg(response.getMsg() + ", " + laptopResp.getMsg());
        }
        return response;
    }

    private DtoResponse update(List<Laptop> laptops) {
        DtoResponse response = new DtoResponse("UPDATE LAPTOP", "FAILED", "Gagal Update Data Laptop");
        if (laptops.size() > 0) {
            List<Laptop> newListLaptop = new ArrayList<>();
            for (Laptop laptop: laptops) {
                laptop.setUpdateAt(new Date(new Date().getTime()));
                newListLaptop.add(laptop);
            }
            newListLaptop = laptopRepository.saveAll(newListLaptop);
            if (newListLaptop.size() > 0) {
                response.setStatus("SUCCESS");
                response.setMsg("Berhasil Update Data Laptop");
                response.setData(newListLaptop);
            }
        }
        return response;
    }
}
