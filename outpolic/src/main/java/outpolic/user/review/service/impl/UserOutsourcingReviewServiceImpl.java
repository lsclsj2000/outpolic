package outpolic.user.review.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.user.osst.mapper.UserOsstMapper;
import outpolic.user.review.dto.UserOutsourcingReviewDTO;
import outpolic.user.review.service.UserOutsourcingReviewService;

@Service
@RequiredArgsConstructor
public class UserOutsourcingReviewServiceImpl implements UserOutsourcingReviewService{

	private final UserOsstMapper userOsstMapper;

	@Override
	@Transactional
	public boolean createReview(UserOutsourcingReviewDTO reviewDTO) {
		// 1. 새로운 리뷰 코드(PK) 생성
        String newReviewCode = userOsstMapper.getNextReviewCode();
        reviewDTO.setRvwCd(newReviewCode);

        // 2. 리뷰 대상 기업 코드(ent_cd) 조회
        // oscId는 Controller에서 받은 프로젝트 식별 코드(ocd_cd)임
        String enterpriseCode = userOsstMapper.getEnterpriseCodeByOscId(reviewDTO.getOscId());
        
        if (enterpriseCode == null || enterpriseCode.isBlank()) {
            return false; // 비정상 요청
        }
        reviewDTO.setRevieweeEntCd(enterpriseCode);

        // 3. Mapper 호출 (Mapper는 reviewerMbrCd와 revieweeEntCd를 사용)
        int insertedRows = userOsstMapper.insertReview(reviewDTO);

        return insertedRows > 0;
	}
	
	@Override
    public UserOutsourcingReviewDTO getReviewForEdit(String oscId, String mbrCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("oscId", oscId);
        params.put("mbrCd", mbrCd);
        return userOsstMapper.findReviewByOscIdAndMbrCd(params);
    }

    @Override
    @Transactional
    public boolean updateReview(UserOutsourcingReviewDTO reviewDTO) {
        // Controller에서 rvwCd, rvwEvl, rvwCn을 담아 전달하면 그대로 Mapper에 전달
        int updatedRows = userOsstMapper.updateReview(reviewDTO);
        return updatedRows > 0;
    }
}
