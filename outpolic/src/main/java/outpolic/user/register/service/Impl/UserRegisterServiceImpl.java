package outpolic.user.register.service.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.common.domain.Member;
import outpolic.user.register.mapper.UserRegisterMapper;
import outpolic.user.register.service.UserRegisterService;

@Service
@RequiredArgsConstructor
public class UserRegisterServiceImpl implements UserRegisterService {
	
	
	private final UserRegisterMapper userRegisterMapper;
	 private final PasswordEncoder passwordEncoder;
	// 회원 기본키 생성
	@Override
	public String getNextMemberCode() {
		
		return userRegisterMapper.getNextMemberCode();
	}


	// 회원 정보 중복확인
	@Override
	public boolean isUserInfoDuple(String type, String value) {
		switch (type) {
        case "memberNickname":
            return userRegisterMapper.isNickNameDuplicated(value) >0;
        case "memberEmail":
            return userRegisterMapper.isEmailDuplicated(value) >0;
        case "memberTelNo":
            return userRegisterMapper.isTelDuplicated(value) >0;
        case "memberId":
            return userRegisterMapper.isIdDuplicated(value) >0;
        default:
		return false;
		}
	}
	//회원 랜덤닉네임 생성
	@Override
	public String getRandomNickname() {
	    return userRegisterMapper.getRandomNickname();
	}
	
	//회원 비밀번호 암호화
	// 회원가입처리
    @Override
    public int registerMember(Member member) {
        // 👉 비밀번호 암호화
        String rawPw = member.getMemberPw();
        String encodedPw = passwordEncoder.encode(rawPw);
        member.setMemberPw(encodedPw);

        return userRegisterMapper.UserRegister(member);
    }
}


