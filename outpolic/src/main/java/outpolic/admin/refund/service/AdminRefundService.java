package outpolic.admin.refund.service;

import java.util.List;
import java.util.Map;
import outpolic.admin.refund.dto.AdminRefundDTO;

public interface AdminRefundService {
    List<AdminRefundDTO> getRefundHistory(Map<String, Object> params);
}