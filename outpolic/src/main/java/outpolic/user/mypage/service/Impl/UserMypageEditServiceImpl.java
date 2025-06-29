package outpolic.user.mypage.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.mapper.UserMypageEditMapper;
import outpolic.user.mypage.service.UserMypageEditService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMypageEditServiceImpl implements UserMypageEditService {

//	private final UserMypageEditMapper userMypageEditMapper;
	@Autowired
	private UserMypageEditMapper userMypageEditMapper;
	
	@Override
	public UserInfoDTO getUserInfoById(String memberId) {
		
		return userMypageEditMapper.getUserInfoById("user002");
	}

	@Override
	public void editUserInfo(UserInfoDTO userInfo) {
		 log.info("📝 수정 요청 들어온 데이터: {}", userInfo);
		    int result = userMypageEditMapper.updateUserInfo(userInfo);
		    log.info("🧾 수정 쿼리 실행 결과: {}건 반영됨", result);
	}

	@Override
	public List<UserInfoDTO> getMemberList() {

		return null;
	}



	

}
