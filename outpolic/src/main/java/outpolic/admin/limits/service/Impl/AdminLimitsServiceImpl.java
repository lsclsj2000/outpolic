package outpolic.admin.limits.service.Impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.limits.domain.AdminDeclarationFullInfo;
import outpolic.admin.limits.domain.AdminLimits;
import outpolic.admin.limits.domain.AdminLimitsReason;
import outpolic.admin.limits.mapper.AdminLimitsMapper;
import outpolic.admin.limits.service.AdminLimitsService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminLimitsServiceImpl implements AdminLimitsService {
	
	private final AdminLimitsMapper adminLimitsMapper;
	
	// 새로 추가할 메서드 구현
	@Override
	public AdminLimits getMemberAuthorityByMemberCode(String memberCode) {
	    return adminLimitsMapper.selectMemberAuthorityByMemberCode(memberCode);
	}
	
	@Override
	public int updateMemberAuthority(AdminLimits adminLimits) {
	    return adminLimitsMapper.updateMemberAuthority(adminLimits);
	}


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
	public List<AdminLimits> selectAdminLimitAuthority(String keyword, String levelValue) {
		// 회원 권한 검색
		return adminLimitsMapper.selectAdminLimitAuthority(keyword, levelValue);
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
	
	@Override
	public void applySanctionAutomatically(String drcCd, String dtCd, String declCd, String admCd) {
	    log.debug("🚩[제재로직] 호출됨: drcCd={}, dtCd={}, declCd={}, admCd={}", drcCd, dtCd, declCd, admCd);

	    AdminDeclarationFullInfo info = adminLimitsMapper.getDeclarationInfo(declCd);
	    log.debug("🧾[제재로직] 신고 상세 정보: {}", info);

	    if (info == null) {
	        log.warn("⚠️ [제재로직] declCd={} 에 해당하는 신고 정보 없음", declCd);
	        return;
	    }

	    String mbrCd = null;
	    if ("DRC_VALID".equals(drcCd)) {
	        mbrCd = info.getDeclarationTargetCode(); // 유효 신고 → 대상 제재
	    } else if ("DRC_MALICE".equals(drcCd)) {
	        mbrCd = info.getDeclarationMemberCode(); // 악의적 신고 → 신고자 제재
	    } else {
	        log.warn("⚠️ [제재로직] 처리 결과 코드 {} 는 처리되지 않음", drcCd);
	        return;
	    }

	    dtCd = info.getDeclarationTypeCode();  // DB 기준값 우선
	    String drCd = info.getDeclarationReasonCode();

	    log.debug("👤[제재로직] 제재 대상 회원코드: {}", mbrCd);
	    log.debug("📂[제재로직] 신고 타입코드: {}, 신고 사유코드: {}", dtCd, drCd);

	    Integer cumCount = adminLimitsMapper.getCumulativeCount(mbrCd, drCd);
	    log.debug("📈[제재로직] 누적 제재 횟수: {}", cumCount);

	    int nextCount = (cumCount == null) ? 1 : cumCount + 1;

	    AdminLimitsReason reason = adminLimitsMapper.getMatchedLimitsReason(dtCd, drCd, nextCount);
	    log.debug("🔎[제재로직] 매칭된 제재 사유: {}", reason);

	    if (reason == null) {
	        log.warn("⚠️ [제재로직] 매칭되는 제재 사유 없음 → dtCd={}, drCd={}, cnt={}", dtCd, drCd, nextCount);
	        return;
	    }

	    String lrCd = reason.getLimitsReasonCode();
	    String lpCd = reason.getLimitsPeriodCode();

	    Timestamp start = new Timestamp(System.currentTimeMillis());
	    Timestamp end = adminLimitsMapper.getEndDateByPeriod(lpCd, start);
	    int remainingDays = (end == null) ? 9999 : (int) ((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));

	    log.debug("🕒[제재로직] 제재 시작일: {}, 종료일: {}, 잔여일수: {}", start, end, remainingDays);

	    AdminLimits limits = new AdminLimits();
	    limits.setLimitsCode("LMT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
	    limits.setLimitsMemberCode(mbrCd);
	    limits.setDeclarationTypeCode(dtCd);
	    limits.setDeclarationReasonCode(drCd);
	    limits.setLimitsReasonCode(lrCd);
	    limits.setLimitsStartYmdt(start);
	    limits.setLimitsEndYmdt(end);
	    limits.setLimitsClearYmdt(null);
	    limits.setLimitsRmdDays(remainingDays);
	    limits.setLimitsStatus(null); // 필요 시 설정
	    limits.setLimitsTypeRegAdmCode(admCd);

	    log.debug("✅[제재로직] 최종 제재 객체: {}", limits);

	    adminLimitsMapper.insertLimits(limits);
	    log.debug("🛠[제재로직] 제재 기록 insert 완료: {}", limits.getLimitsCode());

	    if (cumCount == null) {
	        adminLimitsMapper.insertCumulative(mbrCd, drCd, nextCount);
	        log.debug("🧮[제재로직] 누적 제재 첫 insert 완료 → mbrCd={}, drCd={}, cnt={}", mbrCd, drCd, nextCount);
	    } else {
	        adminLimitsMapper.updateCumulative(mbrCd, drCd, nextCount);
	        log.debug("🧮[제재로직] 누적 제재 update 완료 → mbrCd={}, drCd={}, cnt={}", mbrCd, drCd, nextCount);
	    }

	    log.info("🎉[제재로직] 제재 처리 완료 → 대상 회원: {}, 제재 사유: {}, 기간 코드: {}", mbrCd, lrCd, lpCd);
	}

	@Override
	public List<AdminLimits> selectAdminLimitsList(String keyword, String searchType, String selectDateType,
			String levelSearch, String startDate, String endDate) {
		// 제재 회원 검색
		return adminLimitsMapper.selectAdminLimitsList(keyword, searchType, selectDateType, levelSearch, startDate, endDate);
	}

	// 제재 자원 조회 - 필터
	@Override
    public List<AdminLimits> getFilteredLimitsTypeList(Map<String, Object> paramMap) {
        return adminLimitsMapper.getFilteredLimitsTypeList(paramMap);
    }

    @Override
    public List<AdminLimits> getFilteredLimitsPeriodList(Map<String, Object> paramMap) {
        return adminLimitsMapper.getFilteredLimitsPeriodList(paramMap);
    }

    @Override
    public List<AdminLimits> getFilteredLimitsReasonList(Map<String, Object> paramMap) {
        return adminLimitsMapper.getFilteredLimitsReasonList(paramMap);
    }



}
