package outpolic.user.review.service;

import outpolic.user.review.dto.UserOutsourcingReviewDTO;

public interface UserOutsourcingReviewService {

	boolean createReview(UserOutsourcingReviewDTO reviewDTO);
	
	UserOutsourcingReviewDTO getReviewForEdit(String ocdCd, String mbrCd);

    boolean updateReview(UserOutsourcingReviewDTO reviewDTO);
    
    
}
