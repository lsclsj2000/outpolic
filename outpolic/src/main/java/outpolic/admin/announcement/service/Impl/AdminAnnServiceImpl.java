package outpolic.admin.announcement.service.Impl;

import java.util.List;

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


}
