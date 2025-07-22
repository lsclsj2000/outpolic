package outpolic.user.review.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import outpolic.user.review.dto.UserReviewMainDTO;

@Mapper
public interface UserReviewMainMapper {

	List<UserReviewMainDTO> selectRecentReviewList();
}
