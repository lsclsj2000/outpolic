package outpolic.admin.settlement.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import outpolic.admin.settlement.dto.AdminSettlementDTO;
import outpolic.admin.settlement.mapper.AdminSettlementMapper;
import outpolic.admin.settlement.service.AdminSettlementService;

@Service
@RequiredArgsConstructor
public class AdminSettlementServiceImpl implements AdminSettlementService {

    private final AdminSettlementMapper adminSettlementMapper;

    @Override
    public List<AdminSettlementDTO> getSettlementHistory(Map<String, Object> params) {
        return adminSettlementMapper.selectSettlementHistory(params);
    }
}