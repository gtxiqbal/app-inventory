package id.co.gtx.repository;

import id.co.gtx.entity.Laptop;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, String> {
    @Query(value = "select cast(coalesce (max (id), '0') as integer ) + 1 from laptop", nativeQuery = true)
    String generateId();

    List<Laptop> findByNameLike(String name);

    List<Laptop> findByMerk(Merk merk);

    List<Laptop> findByMerkAndType(Merk merk, Type type);
}
