package id.co.gtx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.co.gtx.entity.enumz.Dept;
import id.co.gtx.entity.enumz.Divisi;
import id.co.gtx.entity.enumz.Jabatan;
import id.co.gtx.entity.enumz.Status;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@Builder
@Entity
@Table(name = "karyawan")
public class Karyawan implements Serializable, Cloneable{
    private static final long serialVersionUID = -8880234250273986042L;

    @Id
    private Long nik;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "no_hp", nullable = false)
    private String noHp;

    @Enumerated(EnumType.STRING)
    @Column(name = "divisi", nullable = false)
    private Divisi divisi;

    @Enumerated(EnumType.STRING)
    @Column(name = "dept", nullable = false)
    private Dept dept;

    @Enumerated(EnumType.STRING)
    @Column(name = "jabatan", nullable = false)
    private Jabatan jabatan;

    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "tmp_lahir", nullable = false)
    private String tmpLahir;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tgl_lahir", nullable = false)
    private Date tglLahir;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "tgl_masuk", nullable = false)
    private Date tglMasuk;

    @JsonBackReference
    @OneToMany(mappedBy = "karyawan")
    private Set<Sewa> sewas;
}
