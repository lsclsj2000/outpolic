package outpolic.admin.goods.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.admin.goods.dto.AdminGoodsDTO;
import outpolic.admin.goods.mapper.AdminGoodsMapper;
import outpolic.admin.goods.service.AdminGoodsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminGoodsServiceImpl implements AdminGoodsService {

	 private final AdminGoodsMapper adminGoodsMapper;

	 @Override
	 public String registerGoods(AdminGoodsDTO adminGoodsDTO) {
		 int result = adminGoodsMapper.insertGoods(adminGoodsDTO);
		    if (result == 0) {
		        throw new RuntimeException("상품 등록에 실패했습니다");
		    }
		    return adminGoodsDTO.getGdsCd();

	 }

	 @Override
	 public List<AdminGoodsDTO> getGoodsList() {
		 List<AdminGoodsDTO> list = adminGoodsMapper.selectAllGoods();
		 log.info("DTO {}", list);
		 
		 return list;
	 }

	 @Override
	 public AdminGoodsDTO getGoodsInfo(String gdsCd) {
		return adminGoodsMapper.selectGoodsByCode(gdsCd);

	 }

	 @Override
	 public String updateGoods(AdminGoodsDTO adminGoodsDTO) {
		 adminGoodsMapper.updateGoods(adminGoodsDTO);
		 return adminGoodsDTO.getGdsCd();

	 }



}
