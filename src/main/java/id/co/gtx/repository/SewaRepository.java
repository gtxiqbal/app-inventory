package id.co.gtx.repository;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.Sewa;
import id.co.gtx.entity.SewaKey;
import id.co.gtx.entity.dto.DtoKaryawanTotalLaptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SewaRepository extends JpaRepository<Sewa, SewaKey> {
    @Query(value = "select new id.co.gtx.entity.dto.DtoKaryawanTotalLaptop(" +
            "s.karyawan.nik, " +
            "s.karyawan.firstName, " +
            "s.karyawan.lastName, " +
            "sum(s.total)) " +
            "from Sewa s " +
            "group by s.karyawan.nik, s.karyawan.firstName, s.karyawan.lastName")
    List<DtoKaryawanTotalLaptop> findAllKaryawanDistinc();

    @Query(value = "select s.karyawan from Sewa s where s.laptop.id = ?1")
    List<Karyawan> findKaryawanByLaptopId(String id);

    @Query(value = "select s.laptop from Sewa s where s.karyawan.nik = ?1")
    List<Laptop> findLaptopByKaryawanNik(Long nik);

    List<Sewa> findByKaryawanNik(Long nik);

    List<Sewa> findByLaptopId(String id);
}
