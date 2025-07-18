package outpolic.user.withdrawn.service;

import org.springframework.stereotype.Service;

import outpolic.user.withdrawn.dto.UserWithdrawnDTO;

@Service
public interface UserWithdrawnService {
	void withdrawMember(String memberCode, String reason);
}
