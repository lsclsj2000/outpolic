package outpolic.admin.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.admin.goods.dto.AdminGoodsDTO;

@Mapper
public interface AdminGoodsMapper {
    int insertGoods(AdminGoodsDTO adminGoodsDTO);
    List<AdminGoodsDTO> selectAllGoods();
    AdminGoodsDTO selectGoodsByCode(String gdsCd);
    int updateGoods(AdminGoodsDTO adminGoodsDTO);

}
