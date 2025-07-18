package outpolic.admin.goods.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 @Transactional // 트랜잭션 관리
	 public String registerGoods(AdminGoodsDTO adminGoodsDTO) {
		 // 상품 코드 생성 (selectKey를 통해 XML에서 처리되므로 여기서는 제거)
		 // if (adminGoodsDTO.getGdsCd() == null || adminGoodsDTO.getGdsCd().isEmpty()) {
		 // adminGoodsDTO.setGdsCd("GDS_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
		 // }
		 // 등록일시 설정 (Controller에서 설정하므로 여기서는 제거하거나, 필요에 따라 유지)
		 // adminGoodsDTO.setGdsRegYmdt(new Timestamp(System.currentTimeMillis()));

		 int result = adminGoodsMapper.insertGoods(adminGoodsDTO);
		 if (result == 0) {
		     throw new RuntimeException("상품 등록에 실패했습니다");
		 }
		 return adminGoodsDTO.getGdsCd();
	 }

	 @Override
	 public List<AdminGoodsDTO> getGoodsList() {
		 List<AdminGoodsDTO> list = adminGoodsMapper.selectAllGoods();
		 //log.info("DTO {}", list);	상품 데이터 조회되는지 로그 찍기
		 
		 return list;
	 }

	 @Override
	 public AdminGoodsDTO getGoodsInfo(String gdsCd) {
		return adminGoodsMapper.selectGoodsByCode(gdsCd);
	 }

	 @Override
	 @Transactional // 트랜잭션 관리
	 public String updateGoods(AdminGoodsDTO adminGoodsDTO) {
		 // 수정 관리자 코드와 수정일시 설정
		 // 실제 관리자 코드 (세션 등에서 가져와야 함)는 Controller에서 DTO에 설정하는 것이 더 적합합니다.
		 // 여기서는 임시로 "ADM_C001"로 설정하거나, Controller에서 받은 값을 그대로 사용합니다.
		 adminGoodsDTO.setGdsMdfcnAdmCd("ADM_C001"); // 예시: 실제 관리자 코드로 변경 필요
		 adminGoodsDTO.setGdsMdfcnYmdt(new Timestamp(System.currentTimeMillis()));

		 int result = adminGoodsMapper.updateGoods(adminGoodsDTO);
		 if (result == 0) {
			 throw new RuntimeException("상품 수정에 실패했습니다. 상품 코드를 확인하세요: " + adminGoodsDTO.getGdsCd());
		 }
		 return adminGoodsDTO.getGdsCd();
	 }



}
