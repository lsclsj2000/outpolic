package outpolic.admin.osst.service;

import java.util.List;

import outpolic.admin.osst.domain.AdminOsst;

public interface AdminOsstService {
	
	// 외주 진행 목록 조회
	List<AdminOsst> getAdminOsstList();
}
