package outpolic.admin.announcement.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import outpolic.admin.announcement.domain.AdminAnn;

public interface AdminAnnService {
	
	// 공지사항 수정 저장
	int updateAdminAnn(AdminAnn adminAnn);
	
	// 공지사항 수정 모달창
	AdminAnn getAdminAnnDetail(String annCode);
	
	// 공지사항 내역 조회
	List<AdminAnn> getAdminAnnList();
	
	// 공지사항 내역 조회 - 필터
	List<AdminAnn> getAdminAnnListFiltered(Map<String, Object> paramMap);
	
	// 공지사항 등록
    void addAdminAnnWrite(AdminAnn ann, MultipartFile[] attachmentFile);
}