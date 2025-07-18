package outpolic.user.portfolioInquiry.service;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;

public interface PortfolioInquiryService {
	
	/**
	 * 포트폴리오 문의 생성
	 * @param inquiryDto 컨트롤러에서 받은 문의 정보 DTO
	 * @return 생성된 문의 정보가 담긴 DTO
	 */
	OutsourcingRequestDTO createInquiry(OutsourcingRequestDTO inquiryDto);

}
