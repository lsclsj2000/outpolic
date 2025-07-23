package outpolic.admin.settlement.service;

import java.util.List;
import java.util.Map;

import outpolic.admin.settlement.dto.AdminSettlementDTO;

public interface AdminSettlementService {

	List<AdminSettlementDTO> getSettlementHistory(Map<String, Object> params);
}
