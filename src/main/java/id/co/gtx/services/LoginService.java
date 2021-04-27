package id.co.gtx.services;

import id.co.gtx.entity.dto.DtoResponse;

public interface LoginService {
	DtoResponse doLogin(String username, String password);
}
