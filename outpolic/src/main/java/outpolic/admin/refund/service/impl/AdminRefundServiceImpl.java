package outpolic.admin.refund.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import outpolic.admin.refund.dto.AdminRefundDTO;
import outpolic.admin.refund.mapper.AdminRefundMapper;
import outpolic.admin.refund.service.AdminRefundService;

@Service
@RequiredArgsConstructor
public class AdminRefundServiceImpl implements AdminRefundService {

    private final AdminRefundMapper adminRefundMapper;

    @Override
    public List<AdminRefundDTO> getRefundHistory(Map<String, Object> params) {
        return adminRefundMapper.selectRefundHistory(params);
    }
}