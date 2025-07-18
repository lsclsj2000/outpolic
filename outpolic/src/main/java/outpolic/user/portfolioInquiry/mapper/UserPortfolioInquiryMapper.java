package outpolic.user.portfolioInquiry.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;

@Mapper
public interface UserPortfolioInquiryMapper {
	
	/**
	 * outsourcing_contract_details 테이블에 새로운 문의 내역을 삽입합니다.
	 * @param inquiryDto 저장할 문의 정보
	 */
	void inesrtInquiry(OutsourcingRequestDTO inquiryDto);
    OutsourcingRequestDTO createInquiry(OutsourcingRequestDTO inquiryDto);

	String findLatestOcdCd();
	void insertInquiry(OutsourcingRequestDTO inquiryDto);
}
