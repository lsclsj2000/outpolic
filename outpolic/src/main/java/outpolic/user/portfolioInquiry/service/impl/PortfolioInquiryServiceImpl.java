package outpolic.user.portfolioInquiry.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.user.portfolioInquiry.mapper.UserPortfolioInquiryMapper;
import outpolic.user.portfolioInquiry.service.PortfolioInquiryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioInquiryServiceImpl implements PortfolioInquiryService {

    private final UserPortfolioInquiryMapper inquiryMapper;

    @Override
    @Transactional
    public OutsourcingRequestDTO createInquiry(OutsourcingRequestDTO inquiryDto) { // <<< 여기에 변수명(inquiryDto)을 추가했습니다.
        // 1. 새로운 요청 코드(ocd_cd) 생성
        String latestOcdCd = inquiryMapper.findLatestOcdCd();
        int nextNum = 1;
        if (latestOcdCd != null && latestOcdCd.startsWith("OCD_C")) {
            try {
                nextNum = Integer.parseInt(latestOcdCd.substring(5)) + 1;
            } catch (NumberFormatException e) {
                log.warn("Failed to parse latestOcdCd: {}", latestOcdCd, e);
            }
        }
        String newOcdCd = String.format("OCD_C%05d", nextNum);
        inquiryDto.setOcd_cd(newOcdCd);

        // 2. Mapper를 호출하여 DB에 문의 내역 저장
        inquiryMapper.insertInquiry(inquiryDto);
        log.info("새 포트폴리오 문의가 생성되었습니다. 코드: {}", newOcdCd);

        return inquiryDto;
    }
}