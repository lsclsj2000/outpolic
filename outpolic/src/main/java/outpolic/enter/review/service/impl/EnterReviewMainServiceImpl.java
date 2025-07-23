package outpolic.enter.review.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import outpolic.enter.review.dto.EnterReviewMainDTO;
import outpolic.enter.review.mapper.EnterReviewMainMapper;
import outpolic.enter.review.service.EnterReviewMainService;

@Service
@RequiredArgsConstructor
public class EnterReviewMainServiceImpl implements EnterReviewMainService{

	private final EnterReviewMainMapper enterReviewMainMapper;
	
	@Override
	public List<EnterReviewMainDTO> getRecentReviewList(){
		return enterReviewMainMapper.selectRecentReviewList();
	}
}
