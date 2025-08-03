package outpolic.admin.announcement.service.Impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.announcement.domain.AdminAnn;
import outpolic.admin.announcement.mapper.AdminAnnMapper;
import outpolic.admin.announcement.service.AdminAnnService;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminAnnServiceImpl implements AdminAnnService {
	
	private final AdminAnnMapper adminAnnMapper;

	@Override
	public List<AdminAnn> getAdminAnnList() {
		// 공지사항 내역 조회
		List<AdminAnn> adminAnnList = adminAnnMapper.getAdminAnnList();
		return adminAnnList;
	}
	
	@Override
	public AdminAnn getAdminAnnDetail(String annCode) {
		// 공지사항 수정 모달창
	    return adminAnnMapper.getAdminAnnDetail(annCode);
	}
	
	@Override
	public int updateAdminAnn(AdminAnn adminAnn) {
		// 공지사항 수정 저장
	    return adminAnnMapper.updateAdminAnn(adminAnn);
	}
	
	@Override
	public List<AdminAnn> getAdminAnnListFiltered(Map<String, Object> paramMap) {
		// 공지사항 내역 조회 - 필터
	    return adminAnnMapper.getAdminAnnListFiltered(paramMap);
	}


}
