package outpolic.enter.review.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.review.dto.EnterReviewMainDTO;


@Mapper
public interface EnterReviewMainMapper {

	List<EnterReviewMainDTO> selectRecentReviewList();
}
