package outpolic.admin.refund.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.refund.dto.AdminRefundDTO;

@Mapper
public interface AdminRefundMapper {
	List<AdminRefundDTO> selectRefundHistory(Map<String, Object> params);
}
