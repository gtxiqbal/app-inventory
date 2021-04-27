package id.co.gtx.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import id.co.gtx.entity.enumz.Merk;
import id.co.gtx.entity.enumz.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "laptop")
public class Laptop implements Serializable, Cloneable {
    private static final long serialVersionUID = -2458628417849876883L;

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "merk", nullable = false)
    private Merk merk;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Column(name = "cpu", nullable = false)
    private Double cpu;

    @Column(name = "ram", nullable = false)
    private Double ram;

    @Column(name = "vga", nullable = false)
    private Double vga;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "release", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date release;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date updateAt;

    @JsonBackReference
    @OneToMany(mappedBy = "laptop")
    private Set<Sewa> sewa;
}
