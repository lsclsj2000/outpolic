package outpolic.enter.goods.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.enter.goods.dto.EnterGoodsDTO;
import outpolic.enter.goods.mapper.EnterGoodsMapper;
import outpolic.enter.goods.service.EnterGoodsService;

@Service
@RequiredArgsConstructor
public class EnterGoodsServiceImpl implements EnterGoodsService {
	
	private final EnterGoodsMapper enterGoodsMapper;

    @Override
    public List<EnterGoodsDTO> getActiveProductsByTarget(String targetMemberType) {
        return enterGoodsMapper.selectActiveProductsByTarget(targetMemberType);
    }
}
