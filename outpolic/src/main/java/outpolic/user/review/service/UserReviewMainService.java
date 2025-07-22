package outpolic.user.review.service;

import java.util.List;

import outpolic.user.review.dto.UserReviewMainDTO;

public interface UserReviewMainService {

	List<UserReviewMainDTO> getRecentReviewList();
}
