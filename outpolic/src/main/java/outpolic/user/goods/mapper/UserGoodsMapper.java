package outpolic.user.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.goods.dto.UserGoodsDTO;

@Mapper
public interface UserGoodsMapper {
    List<UserGoodsDTO> selectActiveProductsByTarget(String targetMemberType);

}
