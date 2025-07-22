package outpolic.admin.goods.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.goods.dto.AdminGoodsDTO;

@Mapper
public interface AdminGoodsMapper {
	int insertGoods(AdminGoodsDTO adminGoodsDTO);
    // selectAllGoods 메소드에 Map 파라미터를 받을 수 있도록 수정
    List<AdminGoodsDTO> selectAllGoods(Map<String, Object> params);
    AdminGoodsDTO selectGoodsByCode(String gdsCd);
    int updateGoods(AdminGoodsDTO adminGoodsDTO);

}
