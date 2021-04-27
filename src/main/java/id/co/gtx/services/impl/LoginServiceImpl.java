package id.co.gtx.services.impl;

import id.co.gtx.entity.Login;
import id.co.gtx.entity.dto.DtoResponse;
import id.co.gtx.repository.LoginRepository;
import id.co.gtx.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	LoginRepository loginRepository;

	@Override
	public DtoResponse doLogin(String username, String password) {
		DtoResponse response = new DtoResponse("LOGIN USER", "FAILED", "Username Tidak Detmukan.");
		Optional<Login> checkLogin = loginRepository.findById(username);
		if (!checkLogin.isPresent())
			return response;

		Login login = checkLogin.get();
		if (!login.getPassword().equals(password)) {
			response.setMsg("Password Salah.");
			return response;
		}
		response.setStatus("SUCCESS");
		response.setMsg("Berhasil Login.");
		response.setData(login);
		return response;
	}
}
