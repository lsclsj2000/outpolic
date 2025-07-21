package outpolic.admin.limits.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.mapper.AdminLimitsMapper;
import outpolic.admin.limits.service.AdminLimitsService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminLimitsServiceImpl implements AdminLimitsService {
	
	private final AdminLimitsMapper adminLimitsMapper;

	@Override
	public List<AdminLimits> getAdminLimitsTypeList() {
		// 제재 타입 자원 조회
		List<AdminLimits> adminLimitsTypeList = adminLimitsMapper.getAdminLimitsTypeList();
		log.info("Fetched AdminLimitsTypeList: {}", adminLimitsTypeList); // 이 줄 추가
		return adminLimitsTypeList;
	}

	@Override
	public List<AdminLimits> getAdminLimitsReasonList() {
		// 제재 사유 자원 조회
		List<AdminLimits> adminLimitsReasonList = adminLimitsMapper.getAdminLimitsReasonList();
		return adminLimitsReasonList;
	}
	
	@Override
	public List<AdminLimits> getAdminLimitsPeriodList() {
		// 제재 기간 자원 조회
		List<AdminLimits> adminLimitsPeriodList = adminLimitsMapper.getAdminLimitsPeriodList();
		log.info("Fetched AdminLimitsPeriodList: {}", adminLimitsPeriodList); // 이 줄 추가
		return adminLimitsPeriodList;
	}

	@Override
	public List<AdminLimits> getAdminLimitsList() {
		// 제재 내역 조회
		List<AdminLimits> adminLimitsList = adminLimitsMapper.getAdminLimitsList();
		
		return adminLimitsList;
	}

	@Override
	public List<AdminLimits> getAdminLimitsAuthorityList() {
		// 회원 권한 조회
		List<AdminLimits> adminLimitsAuthorityList = adminLimitsMapper.getAdminLimitsAuthorityList();
		
		return adminLimitsAuthorityList;
	}
	
	@Override
	public int registerLimitsType(AdminLimits adminLimits) {
		// 제재 타입 등록
		return adminLimitsMapper.insertLimitsType(adminLimits);
	}

	@Override
	public int updateLimitsType(AdminLimits adminLimits) {
		// 제재 타입 수정
		return adminLimitsMapper.updateLimitsType(adminLimits);
	}

	@Override
	public AdminLimits getLimitsTypeById(String limitsTypeCode) {
		// 특정 제재 타입 조회 (수정 모달용)
		return adminLimitsMapper.selectLimitsTypeById(limitsTypeCode);
	}
	
	@Override
	public int registerLimitsPeriod(AdminLimits adminLimits) {
		// 제재 기간 등록
		return adminLimitsMapper.insertLimitsPeriod(adminLimits);
	}

	@Override
	public int updateLimitsPeriod(AdminLimits adminLimits) {
		// 제재 기간 수정
		return adminLimitsMapper.updateLimitsPeriod(adminLimits);
	}

	@Override
	public AdminLimits getLimitsPeriodById(String limitsPeriodCode) {
		// 특정 제재 기간 조회 (수정 모달용)
		return adminLimitsMapper.selectLimitsPeriodById(limitsPeriodCode);
	}
	
	@Override
	public int registerLimitsReason(AdminLimits adminLimits) {
		// 제재 사유 등록
	    return adminLimitsMapper.insertLimitsReason(adminLimits);
	}

	@Override
	public int updateLimitsReason(AdminLimits adminLimits) {
		// 제재 사유 수정
	    return adminLimitsMapper.updateLimitsReason(adminLimits);
	}

	@Override
	public AdminLimits getLimitsReasonById(String limitsReasonCode) {
		// 특정 제재 사유 조회 (수정 모달용)
	    return adminLimitsMapper.selectLimitsReasonById(limitsReasonCode);
	}

	@Override
	public List<AdminLimits> getDeclarationTypeList() {
		// 신고 타입 목록 조회
	    return adminLimitsMapper.getDeclarationTypeList();
	}

	@Override
	public List<AdminLimits> getDeclarationReasonList(String declarationTypeCode) {
	    List<AdminLimits> reasons = adminLimitsMapper.getDeclarationReasonList(declarationTypeCode);
	    
	    // 여기에 로그 추가: (logger 대신 log 사용)
	    log.info("DEBUG: 신고 타입 [{}]에 대한 신고 사유 개수: {}", declarationTypeCode, reasons.size());
	    if (reasons != null && !reasons.isEmpty()) {
	        reasons.forEach(reason -> 
	            log.info("  DEBUG: - 코드: {}, 이름: {}", reason.getDeclarationReasonCode(), reason.getDeclarationReasonName())
	        );
	    } else {
	        log.info("  DEBUG: - 반환된 신고 사유 데이터가 없습니다.");
	    }
	    
	    return reasons;
	}


}
