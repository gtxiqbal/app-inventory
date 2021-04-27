package id.co.gtx.repository;

import id.co.gtx.entity.Karyawan;
import id.co.gtx.entity.enumz.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KaryawanRepository extends JpaRepository<Karyawan, Long> {

    @Query(value = "select max(k.nik) as nik from Karyawan k where k.status = ?1 or k.status = ?2")
    Long getGenerateNik(Status status1, Status status2);

    List<Karyawan> findByFirstNameLike(String firstName);

    List<Karyawan> findByLastNameLike(String lastName);

    Karyawan findByEmail(String email);
}
