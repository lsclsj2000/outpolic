package outpolic.admin.goods.service;

import java.util.List;

import outpolic.admin.goods.dto.AdminGoodsDTO;

public interface AdminGoodsService {
    String registerGoods(AdminGoodsDTO adminGoodsDTO);
    List<AdminGoodsDTO> getGoodsList();
    AdminGoodsDTO getGoodsInfo(String gdsCd);
    String updateGoods(AdminGoodsDTO adminGoodsDTO);

}
