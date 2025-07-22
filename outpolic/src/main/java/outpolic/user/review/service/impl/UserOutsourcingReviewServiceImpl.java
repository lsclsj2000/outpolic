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
		// 1. 새로운 리뷰 코드(PK) 생성 (예: 'RVW_C00007')
        String newReviewCode = userOsstMapper.getNextReviewCode();
        reviewDTO.setRvwCd(newReviewCode);

        // 2. 리뷰 대상 기업 코드(ent_cd) 조회
        // 작성자님께서 확인해주신 대로, review 테이블에는 리뷰 대상의 ent_cd가 저장되어야 합니다.
        // osc_id (ocd_cd와 동일)를 사용하여 outsourcing_contract_details 테이블에서 ent_cd를 조회합니다.
        String enterpriseCode = userOsstMapper.getEnterpriseCodeByOscId(reviewDTO.getOscId());
        
        // 2.1. 방어 코드: 만약 유효하지 않은 oscId로 인해 기업코드를 찾지 못하면 실패 처리
        if (enterpriseCode == null || enterpriseCode.isBlank()) {
            // 이 경우는 비정상적인 요청이므로 false를 반환하여 작업을 중단합니다.
            return false; 
        }
        reviewDTO.setRevieweeEntCd(enterpriseCode);

        // 3. 완성된 DTO를 사용하여 리뷰 정보 DB에 저장
        // mapper의 insertReview는 reviewerMbrCd, revieweeEntCd 필드를 사용합니다.
        int insertedRows = userOsstMapper.insertReview(reviewDTO);

        // 4. INSERT 쿼리가 성공적으로 1개 행을 추가했는지 여부를 반환
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
        int updatedRows = userOsstMapper.updateReview(reviewDTO);
        return updatedRows > 0;
    }
}
