package id.co.gtx.services.impl;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.Sewa;
import id.co.gtx.entity.dto.DtoKaryawanTotalLaptop;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.repository.SewaRepository;
import id.co.gtx.services.LaptopService;
import id.co.gtx.services.SewaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zkoss.zk.ui.WrongValueException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("sewaService")
public class SewaServiceImpl implements SewaService {

    @Autowired
    private SewaRepository sewaRepository;

    @Autowired
    private LaptopService laptopService;

    @Override
    public DtoResponse findAll() {
        DtoResponse response = new DtoResponse<>("GET ALL SEWA", "FAILED", "Data Inventarisir Kosong");
        List<DtoKaryawanTotalLaptop> sewas = sewaRepository.findAllKaryawanDistinc();
        if (sewas.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Semua Data Inventarisis");
            response.setData(sewas);
        }
        return response;
    }

    @Override
    public DtoResponse findByNik(Long nik) {
        DtoResponse response = new DtoResponse<>("GET SEWA", "FAILED", "NIK: " + nik + " Tidak Ditemukan.");
        List<Sewa> sewas = sewaRepository.findByKaryawanNik(nik);
        if (sewas.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Inventarisir By NIK: " + nik);
            response.setData(sewas);
        }
        return response;
    }

    @Override
    public DtoResponse findKaryawanByLaptopId(String id) {
        DtoResponse response = new DtoResponse<>("GET SEWA", "FAILED", "Gagal Menampilkan Data Karyawan, ID Laptop Tidak Ditemukan");
        List<Karyawan> karyawans = sewaRepository.findKaryawanByLaptopId(id);
        if (karyawans.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Karyawan By ID Laptop");
            response.setData(karyawans);
        }
        return response;
    }

    @Override
    public DtoResponse findLaptopByKaryawanNik(Long nik) {
        DtoResponse response = new DtoResponse<>("GET SEWA", "FAILED", "Gagal Menampilkan Data Laptop, NIK Karyawan Tidak Ditemukan");
        List<Laptop> laptops = sewaRepository.findLaptopByKaryawanNik(nik);
        if (laptops.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Laptop By NIK Karyawan");
            response.setData(laptops);
        }
        return response;
    }

    @Override
    @Transactional
    public DtoResponse create(List<Sewa> sewas) {
        List<Sewa> newSewa = new ArrayList<>();
        for (Sewa sewa: sewas) {
            List<Sewa> sewaList = sewaRepository.findByKaryawanNik(sewa.getKaryawan().getNik());
            if (sewaList.size() > 0) {
                newSewa.add(sewa);
                sewaRepository.deleteAll(sewaList);
            }
        }

        if (newSewa.size() == 0 || newSewa.size() < sewas.size())
            newSewa = sewas;

        DtoResponse response = new DtoResponse("SAVE LIST SEWA", "SUCCESS", "Berhasil Menyimpan Data Inventarisir");
        List<Laptop> laptops = new ArrayList<>();
        for (Sewa sewa: newSewa) {
            DtoResponse laptopService = this.laptopService.findById(sewa.getLaptop().getId());
            if (laptopService.getStatus().equals("SUCCESS")) {
                Laptop laptop = (Laptop) laptopService.getData();
                if (sewa.getTotal() > laptop.getTotal()) {
                    response.setStatus("FAILED");
                    response.setMsg("Input Jumlah Laptop " + laptop.getName() + ", tidak boleh lebih dari stok");
                    throw new WrongValueException("Input Jumlah Laptop " + laptop.getName() + ", tidak boleh lebih dari stok");
//                    return response;
                } else {
                    laptop.setTotal(laptop.getTotal() - sewa.getTotal());
                    laptops.add(laptop);
                }
            }
        }

        if (laptopService.batchUpdate(laptops).getStatus().equals("FAILED")) {
            throw new WrongValueException("Gagal Update Stok Laptop");
        }
        response.setData(sewaRepository.saveAll(newSewa));
        return response;
    }

    @Override
    @Transactional
    public DtoResponse delete(Long nik) {
        DtoResponse response = new DtoResponse("DELETE SEWA", "FAILED", "Gagal Hapus Data Inventarisir");
        List<Sewa> sewas = sewaRepository.findByKaryawanNik(nik);
        if (sewas.size() > 0 ) {
            List<Laptop> laptops = new ArrayList<>();
            for (Sewa sewa: sewas) {
                DtoResponse laptopResp = laptopService.findById(sewa.getLaptop().getId());
                if (laptopResp.getStatus().equals("SUCCESS")) {
                    Laptop laptop = (Laptop) laptopResp.getData();
                    laptop.setTotal(laptop.getTotal() + sewa.getTotal());
                    laptops.add(laptop);
                }
            }
            if (laptopService.batchUpdate(laptops).getStatus().equals("FAILED")) {
                throw new WrongValueException("Gagal Update Rollback Stok Laptop");
            }
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Hapus Data Inventarisir By NIK: " + nik);
            response.setData(sewas);
            sewaRepository.deleteAll(sewas);
        }
        return response;
    }

    @Override
    @Transactional
    public DtoResponse delete(String id) {
        DtoResponse response = new DtoResponse("DELETE SEWA", "FAILED", "Gagal Hapus Data Inventarisir");
        List<Sewa> sewas = sewaRepository.findByLaptopId(id);
        if (sewas.size() > 0 ) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Hapus Data Inventarisir By ID Laptop: " + id);
            response.setData(sewas);
            sewaRepository.deleteAll(sewas);
        }
        return response;
    }
}
