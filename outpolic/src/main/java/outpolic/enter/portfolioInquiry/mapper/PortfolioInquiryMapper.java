package outpolic.enter.portfolioInquiry.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.portfolioInquiry.domain.PortfolioInquiryDTO;
import outpolic.enter.portfolioInquiry.domain.PortfolioInquiryViewDTO;

@Mapper
public interface PortfolioInquiryMapper {
	
	// 문의 상세 목록 조회
	void insertInquiry(PortfolioInquiryDTO inquiry);
	
	void updateChatRoomId(@Param("ocd_cd") String ocd_cd, @Param("chr_cd") String chr_cd);
	
	List<PortfolioInquiryViewDTO> findSentInquiry(String inquirierId);
	
	PortfolioInquiryViewDTO findInquiryDetailById(String inquiryId);
	
	List<PortfolioInquiryViewDTO> findReceivedInquiry(String supplierEntCd);
	
	String findLatestOcdCd();
	
	String findEntCdByMbrCd(String mbrCd);
	
	PortfolioInquiryViewDTO findInquiryByOcdCd(String ocdCd);
	
	
}
