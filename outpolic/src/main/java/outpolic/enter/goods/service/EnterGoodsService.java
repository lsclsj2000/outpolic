package outpolic.enter.goods.service;

import java.util.List;

import outpolic.enter.goods.dto.EnterGoodsDTO;

public interface EnterGoodsService {
	List<EnterGoodsDTO> getActiveProductsByTarget(String targetMemberType);
}
