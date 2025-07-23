package outpolic.admin.goods.service;

import java.util.List;
import java.util.Map;

import outpolic.admin.goods.dto.AdminGoodsDTO;

public interface AdminGoodsService {
    String registerGoods(AdminGoodsDTO adminGoodsDTO);
    List<AdminGoodsDTO> getGoodsList(Map<String, Object> params);
    AdminGoodsDTO getGoodsInfo(String gdsCd);
    String updateGoods(AdminGoodsDTO adminGoodsDTO);

}
