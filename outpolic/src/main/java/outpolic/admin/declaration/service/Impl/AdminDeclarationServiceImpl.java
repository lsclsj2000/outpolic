package outpolic.admin.declaration.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.declaration.domain.AdminDeclaration;
import outpolic.admin.declaration.domain.SubmissionAttachment;
import outpolic.admin.declaration.mapper.AdminDeclarationMapper;
import outpolic.admin.declaration.service.AdminDeclarationService;
import outpolic.admin.limits.mapper.AdminLimitsMapper;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminDeclarationServiceImpl implements AdminDeclarationService {
	
	private final AdminDeclarationMapper adminDeclarationMapper;
	private final AdminLimitsMapper adminLimitsMapper;

	@Override
	public List<AdminDeclaration> getAdminDeclarationList() {
		// 신고 내역 목록 조회
		List<AdminDeclaration> adminDeclarationList = adminDeclarationMapper.getAdminDeclarationList();
		return adminDeclarationList;
	}

	@Override
	public List<AdminDeclaration> getAdminDeclarationTypeList() {
		// 신고 타입 자원 조회
		List<AdminDeclaration> adminDeclarationList= adminDeclarationMapper.getAdminDeclarationTypeList();
		
		return adminDeclarationList;
	}

	@Override
	public List<AdminDeclaration> getAdminDeclarationReasonList() {
		// 신고 사유 자원 조회
		List<AdminDeclaration> adminDeclarationReasonList = adminDeclarationMapper.getAdminDeclarationReasonList();
		return adminDeclarationReasonList;
	}

	@Override
	public List<AdminDeclaration> getAdminDeclarationResultList() {
		// 신고 처리 결과 자원 조회
		List<AdminDeclaration> adminDeclarationResultList = adminDeclarationMapper.getAdminDeclarationResultList();
		return adminDeclarationResultList;
	}
	
	@Override
	public void insertDeclarationType(AdminDeclaration declaration) {
		// 신고 타입 등록
		adminDeclarationMapper.insertDeclarationType(declaration);
	}
	
	@Override
	public void insertDeclarationReason(AdminDeclaration declaration) {
		// 신고 사유 등록
		adminDeclarationMapper.insertDeclarationReason(declaration);
	}

	@Override
	public void insertDeclarationResult(AdminDeclaration declaration) {
		// 신고처리결과 등록
		adminDeclarationMapper.insertDeclarationResult(declaration);
	}
	
	@Override
	public void updateDeclarationType(AdminDeclaration adminDeclaration) {
		adminDeclarationMapper.updateDeclarationType(adminDeclaration);
	}

	@Override
	public AdminDeclaration getDeclarationTypeByCode(String code) {
		// 신고 타입 수정팝업창 조회
		return adminDeclarationMapper.getDeclarationTypeByCode(code);
	}

	@Override
	public void updateDeclarationReason(AdminDeclaration adminDeclaration) {
		// 신고 사유 수정
		adminDeclarationMapper.updateDeclarationReason(adminDeclaration);
	}

	@Override
	public void updateDeclarationResult(AdminDeclaration adminDeclaration) {
		// 신고처리결과코드 수정
		adminDeclarationMapper.updateDeclarationResult(adminDeclaration);
	}
	
	@Override
	public AdminDeclaration getDeclarationReasonByCode(String code) {
		// 신고 사유 수정팝업창 조회
		return adminDeclarationMapper.getDeclarationReasonByCode(code);
	}

	@Override
	public AdminDeclaration getDeclarationResultByCode(String code) {
		// 신고처리결과코드 수정팝업창 조회
		return adminDeclarationMapper.getDeclarationResultByCode(code);
	}

	@Override
	public AdminDeclaration getAdminDeclarationDetail(String declarationCode) {
		// 신고 수정 팝업창 조회 (첨부파일 목록 로직 추가)
		AdminDeclaration declarationDetail = adminDeclarationMapper.getAdminDeclarationDetail(declarationCode);
		if (declarationDetail != null) {
			// 해당 신고 코드에 대한 첨부파일 목록 조회
			List<SubmissionAttachment> attachments = adminDeclarationMapper.getSubmissionAttachmentsByDeclarationCode(declarationCode);
			declarationDetail.setAttachments(attachments);
		}
		return declarationDetail;
	}

	@Override
	public List<AdminDeclaration> getDeclarationReasonsByTypeCode(String dtCode) {
		// 신고 타입별 신고 사유 조회
		return adminDeclarationMapper.getDeclarationReasonsByTypeCode(dtCode);
	}
	
	@Override
	public List<AdminDeclaration> getDeclarationStatusList() {
		// 신고 처리 상태 조회
		return adminDeclarationMapper.getDeclarationStatusList();
	}
	
	@Override
	public void updateDeclaration(AdminDeclaration adminDeclaration) {
		// 신고 내역 수정 업데이트
		
		AdminDeclaration oldDeclaration = adminDeclarationMapper.getAdminDeclarationDetail(adminDeclaration.getDeclarationCode());
		
		// '요청중'에서 '처리중'으로 상태 변경 시 declaration_process 테이블에 초기 데이터 삽입
		// 이는 dp_cd, decl_cd, adm_cd만 들어가는 초기 상태를 의미합니다.
		if ("SD_INQUIRY_ING".equals(oldDeclaration.getDeclarationStcCode()) &&
			"SD_PROCESS_ING".equals(adminDeclaration.getDeclarationStcCode())) {
			adminDeclarationMapper.insertDeclarationProcess(adminDeclaration);
		}
		
		adminDeclarationMapper.updateDeclaration(adminDeclaration);
	}

	@Override
	public void processDeclaration(AdminDeclaration adminDeclaration) {
		// 신고 처리 내역 저장 및 신고 상태 업데이트
		log.info("신고 처리 시작. 신고 코드: {}", adminDeclaration.getDeclarationCode());
		
		adminDeclarationMapper.updateDeclarationProcessContentAndResult(adminDeclaration);
		log.info("declaration_process 테이블 업데이트 완료. 신고 코드: {}", adminDeclaration.getDeclarationCode());

		adminDeclaration.setDeclarationStcCode("SD_PROCESS_END"); // 상태를 처리완료로 고정
		adminDeclarationMapper.updateDeclarationStatusAndModifier(adminDeclaration); // 이름 변경된 Mapper 메서드 호출
		log.info("declaration 테이블 상태 'SD_PROCESS_END'로 업데이트 및 수정자/수정일시 업데이트 완료. 신고 코드: {}", adminDeclaration.getDeclarationCode());
	}

    @Override
    public void updateDeclarationStatusAndModifier(AdminDeclaration adminDeclaration) {
        adminDeclarationMapper.updateDeclarationStatusAndModifier(adminDeclaration);
    }
    
    public List<AdminDeclaration> getAdminDeclarationListFiltered(Map<String, Object> searchParams) {
    	// 신고 내역 조회 - 필터
        return adminDeclarationMapper.getAdminDeclarationListFiltered(searchParams);
    }
    
    
}
