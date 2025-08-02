package outpolic.admin.announcement.service;

import java.util.List;

import outpolic.admin.announcement.domain.AdminAnn;

public interface AdminAnnService {
	
	// 공지사항 내역 조회
	List<AdminAnn> getAdminAnnList();
}