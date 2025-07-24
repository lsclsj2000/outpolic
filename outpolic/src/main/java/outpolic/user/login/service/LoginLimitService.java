package outpolic.user.login.service;

import org.springframework.stereotype.Service;

import outpolic.user.login.dto.LoginFailDTO;

@Service
public interface LoginLimitService {
	LoginFailDTO getLimitInfo(String memberCode);
}
