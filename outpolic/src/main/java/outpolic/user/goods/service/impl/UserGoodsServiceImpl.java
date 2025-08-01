package outpolic.user.goods.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.user.goods.dto.UserGoodsDTO;
import outpolic.user.goods.mapper.UserGoodsMapper;
import outpolic.user.goods.service.UserGoodsService;

@Service
@RequiredArgsConstructor
public class UserGoodsServiceImpl implements UserGoodsService {
	
	private final UserGoodsMapper userGoodsMapper;
	
	@Override
	public List<UserGoodsDTO> getActiveProductsByTarget(String targetMemberType) {
		return userGoodsMapper.selectActiveProductsByTarget(targetMemberType);
	}

}
