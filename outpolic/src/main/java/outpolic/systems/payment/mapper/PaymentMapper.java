package outpolic.systems.payment.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.systems.payment.dto.SettlementDTO;


@Mapper
public interface PaymentMapper {
    int insertSettlement(SettlementDTO dto);
    
}

