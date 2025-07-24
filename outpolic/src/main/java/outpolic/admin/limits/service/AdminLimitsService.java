	package outpolic.admin.limits.service;
	
	import java.util.List;
	
	import outpolic.admin.limits.domain.AdminLimits;
	
	
	public interface AdminLimitsService {
		
		// 회원 권한 조회
		List<AdminLimits> getAdminLimitsAuthorityList();
		
		// 제재 내역 조회
		List<AdminLimits> getAdminLimitsList();
		
		// 제재 사유 자원 조회
		List<AdminLimits> getAdminLimitsReasonList();
		
		// 제재 기간 자원 조회
		List<AdminLimits> getAdminLimitsPeriodList();
		
		// 제재 타입 자원 조회
		List<AdminLimits> getAdminLimitsTypeList();
		
		// 제재 타입 등록
		int registerLimitsType(AdminLimits adminLimits);
	
		// 제재 타입 수정
		int updateLimitsType(AdminLimits adminLimits);
	
		// 특정 제재 타입 조회 (수정 모달용)
		AdminLimits getLimitsTypeById(String limitsTypeCode);
		
		// 제재 기간 등록
		int registerLimitsPeriod(AdminLimits adminLimits);
	
		// 제재 기간 수정
		int updateLimitsPeriod(AdminLimits adminLimits);
	
		// 특정 제재 기간 조회 (수정 모달용)
		AdminLimits getLimitsPeriodById(String limitsPeriodCode);
		
		// 제재 사유 등록
		int registerLimitsReason(AdminLimits adminLimits);
	
		// 제재 사유 수정
		int updateLimitsReason(AdminLimits adminLimits);
	
		// 특정 제재 사유 조회 (수정 모달용)
		AdminLimits getLimitsReasonById(String limitsReasonCode);
	
		// 신고 타입 목록 조회
		List<AdminLimits> getDeclarationTypeList();
	
		// 신고 사유 목록 조회 (타입별 필터링 가능)
		List<AdminLimits> getDeclarationReasonList(String declarationTypeCode);
		
		// 제재 처리 자동 로직
	    void applySanctionAutomatically(String drcCd, String dtCd, String declCd, String admCd);
	}
