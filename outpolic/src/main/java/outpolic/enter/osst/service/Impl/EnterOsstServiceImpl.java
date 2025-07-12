package outpolic.enter.osst.service.Impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.osst.domain.EnterOsst;
import outpolic.enter.osst.domain.EnterOsstRecord;
import outpolic.enter.osst.mapper.EnterOsstMapper;
import outpolic.enter.osst.service.EnterOsstService;



@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EnterOsstServiceImpl implements EnterOsstService {
	
	private final EnterOsstMapper enterOsstMapper;
	
	@Override
	public List<EnterOsstRecord> getEnterOsstRecord() {
		// 진행 외주 기록 조회
		List<EnterOsstRecord> osstRecord = enterOsstMapper.getEnterOsstRecord();
		return osstRecord;
	}

	@Override
	public EnterOsst getEnterOsstDetail(String osstDetailCode) {
		// 진행 외주 상세 조회
		EnterOsst EnterOsstDetail = enterOsstMapper.getEnterOsstDetail(osstDetailCode);
		return EnterOsstDetail;
	}
	
	@Override
	public List<EnterOsst> getEnterOsstList() {
		// 진행 외주 목록 조회
		List<EnterOsst> osstList = enterOsstMapper.getEnterOsstList();
		return osstList;
	}


}
