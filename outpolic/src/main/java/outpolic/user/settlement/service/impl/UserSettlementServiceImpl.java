package outpolic.user.settlement.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import outpolic.user.settlement.dto.UserSettlement;
import outpolic.user.settlement.mapper.UserSettlementMapper;
import outpolic.user.settlement.service.UserSettlementService;

@Service
public class UserSettlementServiceImpl implements UserSettlementService {

	@Autowired
	private UserSettlementMapper userSettlementMapper;

	// 회원의 결제 내역 정보를 불러온다.
	@Override
	public List<UserSettlement> userSettlementInfo(String mbrCd) {
		
		return userSettlementMapper.selectByMbrCd(mbrCd);
	}

}
