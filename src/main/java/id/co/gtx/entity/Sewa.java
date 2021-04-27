package id.co.gtx.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "sewa")
public class Sewa implements Serializable, Cloneable {
    private static final long serialVersionUID = -1400901759365796703L;

    @EmbeddedId
    private SewaKey id = new SewaKey();

    @ManyToOne
    @MapsId("karyawanNik")
    @JoinColumn(name = "karyawan_nik")
    private Karyawan karyawan;

    @ManyToOne
    @MapsId("laptopId")
    @JoinColumn(name = "laptop_id")
    private Laptop laptop;

    @Column(name = "total", nullable = false)
    private Integer total;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tgl_sewa", nullable = false)
    private Date tglSewa;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tgl_pengembalian", nullable = false)
    private Date tglPengembalian;
}
