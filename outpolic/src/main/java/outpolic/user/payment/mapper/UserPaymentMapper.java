package outpolic.user.payment.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.payment.dto.SettlementDTO;

@Mapper
public interface UserPaymentMapper {
    int insertSettlement(SettlementDTO dto);
    
}

