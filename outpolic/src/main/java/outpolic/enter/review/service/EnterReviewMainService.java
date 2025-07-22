package outpolic.enter.review.service;

import java.util.List;

import outpolic.enter.review.dto.EnterReviewMainDTO;

public interface EnterReviewMainService {

	List<EnterReviewMainDTO> getRecentReviewList();
}
