package id.co.gtx.services.impl;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.entity.enumz.Status;
import id.co.gtx.repository.KaryawanRepository;
import id.co.gtx.services.KaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("karyawanService")
public class KaryawanServiceImpl implements KaryawanService {

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Override
    public Long generateNik() {
        Long generateNik = karyawanRepository.getGenerateNik(Status.TETAP, Status.KONTRAK);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tahun = sdf.format(new Date()).split("-")[0];
        String bulan = sdf.format(new Date()).split("-")[1];
        Long newNik = Long.parseLong(tahun.concat(bulan.concat("001")));
        if (generateNik != null) {
            String tahunNik = String.valueOf(generateNik).substring(0, 4);
            String bulanNik = String.valueOf(generateNik).substring(4, 6);
            Integer nomorNik = Integer.parseInt(String.valueOf(generateNik).substring(6, 9)) + 1;
            String nomor = "000".concat(String.valueOf(nomorNik));
            nomor = nomor.substring(nomor.length() - 3);
            if (tahunNik.equals(tahun)) {
                if (bulanNik.equals(bulan))
                    newNik = Long.parseLong(tahunNik.concat(bulanNik.concat(nomor)));
                else
                    newNik = Long.parseLong(tahunNik.concat(bulan.concat(nomor)));
            }
        }
        return newNik;
    }

    @Override
    public Long generateNikOutsouring() {
        Long generateNik = karyawanRepository.getGenerateNik(Status.OUTSOURCING, Status.OUTSOURCING);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tahun = sdf.format(new Date()).split("-")[0];
        Long newNik = Long.parseLong(tahun.concat("001"));
        if (generateNik != null) {
            String tahunNik = String.valueOf(generateNik).substring(0, 4);
            Integer nomorNik = Integer.parseInt(String.valueOf(generateNik).substring(4, 7)) + 1;
            String nomor = "000".concat(String.valueOf(nomorNik));
            nomor = nomor.substring(nomor.length() - 3);
            if (tahunNik.equals(tahun)) {
                newNik = Long.parseLong(tahunNik.concat(nomor));
            }
        }
        return newNik;
    }

    @Override
    public DtoResponse findAll() {
        DtoResponse response = new DtoResponse("GET ALL KARYAWAN", "FAILED", "Data Karyawan Kosong");
        List<Karyawan> karyawans = karyawanRepository.findAll(Sort.by("nik").ascending());
        if (karyawans.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Semua Daftar Karyawan");
            response.setData(karyawans);
        }
        return response;
    }

    @Override
    public DtoResponse findByNik(Long nik) {
        DtoResponse response = new DtoResponse("GET KARYAWAN", "FAILED", "NIK Karyawan Tidak Ditemukan");
        Optional<Karyawan> karyawan = karyawanRepository.findById(nik);
        if (karyawan.isPresent()) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Karyawan By NIK");
            response.setData(karyawan.get());
        }
        return response;
    }

    @Override
    public DtoResponse findByFirstName(String firstName) {
        DtoResponse response = new DtoResponse("GET KARYAWAN", "FAILED", "Nama Depan Karyawan Tidak Ditemukan");
        List<Karyawan> karyawans = karyawanRepository.findByFirstNameLike("%" + firstName + "%");
        if (karyawans.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Karyawan By Nama Depan");
            response.setData(karyawans);
        }
        return response;
    }

    @Override
    public DtoResponse findByLastName(String lastName) {
        DtoResponse response = new DtoResponse("GET KARYAWAN", "FAILED", "Nama Belakang Karyawan Tidak Ditemukan");
        List<Karyawan> karyawans = karyawanRepository.findByLastNameLike("%" + lastName + "%");
        if (karyawans.size() > 0) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Karyawan By Nama Belakang");
            response.setData(karyawans);
        }
        return response;
    }

    @Override
    public DtoResponse findByEmail(String email) {
        DtoResponse response = new DtoResponse("GET KARYAWAN", "FAILED", "Email Karyawan Tidak Ditemukan");
        Karyawan karyawan = karyawanRepository.findByEmail(email);
        if (karyawan != null) {
            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Menampilkan Data Karyawan By Email");
            response.setData(karyawan);
        }
        return response;
    }

    @Override
    @Transactional
    public DtoResponse createOrUpdate(Karyawan karyawan, boolean isUpdate, Long nikOld) {
        if (!isUpdate)
            return create(karyawan);
        else
            return update(karyawan, nikOld);
    }

    public DtoResponse create(Karyawan karyawan) {
        DtoResponse response = new DtoResponse("SAVE KARYAWAN", "FAILED", "Gagal Menambahkan Data Karyawan Baru");
        DtoResponse resp = findByEmail(karyawan.getEmail());
        if (resp.getStatus().equals("SUCCESS")) {
            response.setMsg(response.getMsg() + ", Email: " + karyawan.getEmail() + " Suda Terdaftar.");
            return response;
        }
        response.setStatus("SUCCESS");
        response.setMsg("Berhasil Menambahkan Data Karyawan Baru, NIK: " + karyawan.getNik());
        response.setData(karyawanRepository.save(karyawan));
        return response;
    }

    public DtoResponse update(Karyawan karyawan, Long nikOld) {
        DtoResponse response = new DtoResponse("UPDATE KARYAWAN", "FAILED", "Gagal Update Data Karyawan");
        DtoResponse resp1 = findByNik(nikOld);
        if (resp1.getStatus().equals("SUCCESS")) {
            Karyawan karyawanOld = (Karyawan) resp1.getData();
            if (!karyawan.getEmail().equals(karyawanOld.getEmail())) {
                DtoResponse resp2 = findByEmail(karyawan.getEmail());
                if (resp2.getStatus().equals("SUCCESS")) {
                    response.setMsg(resp2.getMsg());
                    return response;
                }
            }

            response.setStatus("SUCCESS");
            response.setMsg("Berhasil Update Data Karyawan, NIK: " + karyawan.getNik());
            if (!karyawan.getNik().equals(karyawanOld.getNik())) {
                DtoResponse resp2 = delete(karyawanOld.getNik());
                if (resp2.getStatus().equals("SUCCESS")) {
                    DtoResponse resp3 = create(karyawan);
                    if (resp3.getStatus().equals("SUCCESS")) {
                        response.setData(resp3.getData());
                        return response;
                    } else {
                        response.setStatus("FAILED");
                        response.setMsg(resp3.getMsg());
                    }
                } else {
                    response.setStatus("FAILED");
                    response.setMsg(resp2.getMsg());
                }
            }

            response.setData(karyawanRepository.save(karyawan));
        }
        return response;
    }

    @Override
    @Transactional
    public DtoResponse delete(Long nik) {
        DtoResponse karyawan = findByNik(nik);
        DtoResponse response = new DtoResponse("DELETE KARYAWAN", "FAILED", "Gagal Hapus Data Karyawan, " + karyawan.getMsg());
        if (karyawan.getStatus().equals("SUCCESS")) {
            response.setStatus(karyawan.getStatus());
            response.setMsg("Berhasil Hapus Data Karyawan, NIK: " + ((Karyawan) karyawan.getData()).getNik());
            response.setData(karyawan.getData());
            karyawanRepository.delete((Karyawan) karyawan.getData());
        }
        return response;
    }
}
