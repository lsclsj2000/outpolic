package outpolic.user.settlement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.settlement.dto.UserSettlement;

@Mapper
public interface UserSettlementMapper {
    List<UserSettlement> selectByMbrCd(String mbrCd);
}
