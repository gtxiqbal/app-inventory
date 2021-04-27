package id.co.gtx.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class DtoResponse<T> implements Serializable {
    private static final long serialVersionUID = -4770500556443181765L;

    private String code;
    private String status;
    private String msg;
    private T data;

    public DtoResponse(String code, String status, String msg) {
        this(code, status, msg, null);
    }

    public DtoResponse(String code, String status, String msg, T data) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
