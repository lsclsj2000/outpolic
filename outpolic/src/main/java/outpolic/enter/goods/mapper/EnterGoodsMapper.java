package outpolic.enter.goods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.goods.dto.EnterGoodsDTO;

@Mapper
public interface EnterGoodsMapper {
	/**
     * 회원 유형에 따라 활성화된 상품 목록을 조회합니다.
     * @param targetMemberType "CORPORATE"
     * @return 정렬된 상품 DTO 리스트
     */
    List<EnterGoodsDTO> selectActiveProductsByTarget(String targetMemberType);
}
