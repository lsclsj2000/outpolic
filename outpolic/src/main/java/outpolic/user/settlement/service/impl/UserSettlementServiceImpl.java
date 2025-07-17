package outpolic.user.settlement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import outpolic.user.settlement.dto.UserSettlement;
import outpolic.user.settlement.mapper.UserSettlementMapper;
import outpolic.user.settlement.service.UserSettlementService;

@Service
@Slf4j
public class UserSettlementServiceImpl implements UserSettlementService {

	@Autowired
	private UserSettlementMapper userSettlementMapper;

	// 회원의 결제 내역 정보를 불러온다.
	@Override
	public List<UserSettlement> userSettlementInfo(String mbrCd) {
		// --- 로그 1: 서비스 메서드 진입 및 매퍼 호출 전 mbrCd 확인 ---
		// 이 로그는 mbrCd 값이 서비스 계층으로 제대로 전달되었는지 확인합니다.
		log.info("UserSettlementServiceImpl: userSettlementInfo 메서드 진입. mbrCd: {}", mbrCd);
		
		List<UserSettlement> list = userSettlementMapper.selectByMbrCd(mbrCd);
		
		// --- 로그 2: 매퍼로부터 반환된 리스트 크기 확인 ---
		// 이 로그는 MyBatis 매퍼가 데이터베이스에서 몇 개의 레코드를 가져왔는지 보여줍니다.
		// 이 숫자가 0이라면, DB에 데이터가 없거나 쿼리 조건이 잘못되었을 가능성이 높습니다.
		log.info("UserSettlementServiceImpl: userSettlementMapper.selectByMbrCd로부터 반환된 결제 내역 수: {}", list != null ? list.size() : 0);
		
		// --- 로그 3: 리스트의 첫 번째 항목 내용 확인 (디버그용) ---
		// 이 로그는 리스트에 실제 데이터 객체가 담겨있는지,
		// 그리고 그 객체 안의 필드들이 null이 아닌 값으로 채워져 있는지 확인하는 데 유용합니다.
		// 이 로그를 보려면 application.properties에 'logging.level.outpolic=DEBUG'를 추가해야 합니다.
		if (list != null && !list.isEmpty()) {
            log.debug("UserSettlementServiceImpl: 첫 번째 결제 내역 항목: {}", list.get(0));
        } else {
            log.warn("UserSettlementServiceImpl: 조회된 결제 내역이 없거나 리스트가 비어있습니다.");
        }
		
		return list;
	}

}
