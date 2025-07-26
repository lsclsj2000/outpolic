package outpolic.admin.osst.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.osst.domain.AdminOsst;
import outpolic.admin.osst.mapper.AdminOsstMapper;
import outpolic.admin.osst.service.AdminOsstService;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminOsstServiceImpl implements AdminOsstService {
	
	private final AdminOsstMapper adminOsstMapper; 
	
	@Override
	public List<AdminOsst> getAdminOsstList() {
		// 외주 진행 목록 조회
		List<AdminOsst> adminOsst = adminOsstMapper.getAdminOsstList();
		return adminOsst;
	}

	@Override
	public List<AdminOsst> getOsstStepsByOcdCd(String ocdCd) {
		// 외주 진행 수정 팝업창 데이터 조회
		List<AdminOsst> adminOsstMdfcn = adminOsstMapper.getOsstStepsByOcdCd(ocdCd);
		return adminOsstMdfcn;
	}

	@Override
	public void updateStepStatus(String ocdCd, String stcCd, Integer ospCustYn) {
		// 외주 진행 수정 팝업창 업데이트
		adminOsstMapper.updateStepStatus(ocdCd, stcCd, ospCustYn);
	}
	
	@Override
	public List<AdminOsst> getAdminOsstListFiltered(String field, String keyword, String stepStatus) {
		// 외주 진행 목록 필터 조회
		return adminOsstMapper.getAdminOsstListFiltered(field, keyword, stepStatus);
	}


}
