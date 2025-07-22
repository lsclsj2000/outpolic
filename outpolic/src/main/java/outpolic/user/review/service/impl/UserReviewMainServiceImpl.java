package outpolic.user.review.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.user.review.dto.UserReviewMainDTO;
import outpolic.user.review.mapper.UserReviewMainMapper;
import outpolic.user.review.service.UserReviewMainService;

@Service
@RequiredArgsConstructor
public class UserReviewMainServiceImpl implements UserReviewMainService{

	private final UserReviewMainMapper userReviewMainMapper;
	
	@Override
	public List<UserReviewMainDTO> getRecentReviewList(){
		return userReviewMainMapper.selectRecentReviewList();
	}
}
