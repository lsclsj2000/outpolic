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
		// Controller로부터 전달받은 값은 사실 ocd_cd 임
		String ocdIdFromRequest = reviewDTO.getOscId();

		// 1. 전달받은 ocd_cd를 사용하여 실제 osc_id를 조회한다.
		// (이제 이 쿼리는 'outsourcing_prograss' 테이블을 사용하여 정상적으로 실행됩니다.)
		String realOscId = userOsstMapper.findOscIdByOcdId(ocdIdFromRequest);

		// 1.1. 방어 코드: 유효한 osc_id를 찾지 못하면 실패 처리
        if (realOscId == null || realOscId.isBlank()) {
            return false;
        }
        
        // 1.2. 조회한 실제 osc_id를 DTO에 다시 설정한다.
        reviewDTO.setOscId(realOscId);

		// 2. 새로운 리뷰 코드(PK) 생성
        String newReviewCode = userOsstMapper.getNextReviewCode();
        reviewDTO.setRvwCd(newReviewCode);

        // 3. ent_cd 관련 로직은 없음.

        // 4. 완성된 DTO를 사용하여 DB에 저장
        // (Mapper의 insertReview는 이제 ent_cd 없이, 올바른 osc_id를 가지고 실행됩니다.)
        int insertedRows = userOsstMapper.insertReview(reviewDTO);

        // 5. 성공 여부 반환
        return insertedRows > 0;
	}
	
	@Override
    public UserOutsourcingReviewDTO getReviewForEdit(String ocdCd, String mbrCd) {
		
		String realOscId = userOsstMapper.findOscIdByOcdId(ocdCd);
		
		if (realOscId == null) {
            // 변환할 ID가 없으면, 리뷰도 존재할 수 없으므로 null 반환
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("oscId", realOscId);
        params.put("mbrCd", mbrCd);
        return userOsstMapper.findReviewByOscIdAndMbrCd(params);
    }

    @Override
    @Transactional
    public boolean updateReview(UserOutsourcingReviewDTO reviewDTO) {
        int updatedRows = userOsstMapper.updateReview(reviewDTO);
        return updatedRows > 0;
    }
	

}
