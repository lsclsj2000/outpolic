package outpolic.user.settlement.service;

import java.util.List;

import outpolic.user.settlement.dto.UserSettlement;

public interface UserSettlementService {

	List<UserSettlement> userSettlementInfo(String mbrCd);
}
