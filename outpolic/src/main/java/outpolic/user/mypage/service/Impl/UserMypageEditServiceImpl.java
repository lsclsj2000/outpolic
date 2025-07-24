package outpolic.user.mypage.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.common.dto.OutsourcingReviewDTO;
import outpolic.user.mypage.dto.UserInfoDTO;
import outpolic.user.mypage.mapper.UserMypageEditMapper;
import outpolic.user.mypage.service.UserMypageEditService;
import outpolic.user.outsourcing.dto.UserOsInfoDTO;
import outpolic.user.review.dto.ReviewDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMypageEditServiceImpl implements UserMypageEditService {

	private final UserMypageEditMapper userMypageEditMapper;
	
	@Override
	public UserInfoDTO getUserInfoByCode(String memberCode) {
		
		return userMypageEditMapper.getUserInfoByCode(memberCode);
	}

	@Override
	public void editUserInfo(UserInfoDTO userInfo) {
		 log.info("📝 수정 요청 들어온 데이터: {}", userInfo);
		    int result = userMypageEditMapper.updateUserInfo(userInfo);
		    log.info("🧾 수정 쿼리 실행 결과: {}건 반영됨", result);
	}


	// 회원 닉네임 중복검사 -> 0이 아니고 1이 나오면 중복이 존재한다는 뜻.
	@Override
	public boolean isUserInfoDuple(String type, String memberCode, String memberNickname, String memberEmail, String memberTelNo) {
		switch (type) { 
        case "memberNickname":
            return userMypageEditMapper.isNickNameDuplicated(memberNickname, memberCode);
        case "memberEmail":
            return userMypageEditMapper.isEmailDuplicated(memberEmail, memberCode);
        case "memberTelNo":
            return userMypageEditMapper.isTelDuplicated(memberTelNo, memberCode);
        default:
            return false;
		}
	}

	@Override
	public ReviewDTO getUserReviewByCode(String memberCode) {
		
		return userMypageEditMapper.getUserReviewByCode(memberCode);
	}

	@Override
	public List<OutsourcingReviewDTO> getOutsourcingReviewList(String memberCode) {
		 return userMypageEditMapper.selectUserReviewList(memberCode);
	}

	@Override
	public int selectUserEndedOsByCode(String memberCode) {
		return userMypageEditMapper.selectUserEndedOsByCode(memberCode);
	}
	
	@Override
    public void updateProfileImg(String memberCode, String imagePath) {
        userMypageEditMapper.updateProfileImg(memberCode, imagePath);
    }

}
