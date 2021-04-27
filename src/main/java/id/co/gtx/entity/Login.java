package id.co.gtx.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login")
public class Login implements Serializable, Cloneable {

	@Id
	@Column(name = "username", length = 5, nullable = false)
	private String username;

	@Column(name = "password", length = 16, nullable = false)
	private String password;
}
