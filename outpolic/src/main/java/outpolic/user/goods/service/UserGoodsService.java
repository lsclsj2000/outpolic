package outpolic.user.goods.service;

import java.util.List;

import outpolic.user.goods.dto.UserGoodsDTO;

public interface UserGoodsService {
    List<UserGoodsDTO> getActiveProductsByTarget(String targetMemberType);
}
