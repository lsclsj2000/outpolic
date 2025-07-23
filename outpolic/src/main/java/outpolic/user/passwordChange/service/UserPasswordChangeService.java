package outpolic.user.passwordChange.service;

import org.springframework.stereotype.Service;

import outpolic.common.domain.Member;

@Service
public interface UserPasswordChangeService {
	
	public boolean isCurrentPasswordValid(String memberCode, String rawPw);
	
	public void updatePassword(String memberCode, String rawNewPw);
	
	String getEncodedPassword(String memberCode);


}
