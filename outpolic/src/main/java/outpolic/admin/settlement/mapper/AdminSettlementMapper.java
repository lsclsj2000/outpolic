package outpolic.admin.settlement.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.settlement.dto.AdminSettlementDTO;

@Mapper
public interface AdminSettlementMapper {

	List<AdminSettlementDTO> selectSettlementHistory(Map<String, Object> params);
}
