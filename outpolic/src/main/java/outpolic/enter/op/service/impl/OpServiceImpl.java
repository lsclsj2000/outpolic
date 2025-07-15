package outpolic.enter.op.service.impl;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import outpolic.enter.op.mapper.OpMapper;
import outpolic.enter.op.service.OpService;
import outpolic.enter.outsourcing.domain.EnterOutsourcing;

/**
 * @Service: 이 클래스가 서비스 로직을 담당하는 Spring에게 알려줍니다.
 * @RequiredArgsConstructor: final 필드를 위한 생성자를 자동으로 만듭니다.
 * (의존성 주입)
 */
@Service
@RequiredArgsConstructor
public class OpServiceImpl implements OpService {
	
	private static final Logger logger = LoggerFactory.getLogger(OpServiceImpl.class);

	// 데이터베이스 작업을 위해 OpMapper를 주입받음
	private final OpMapper opMapper;
	@Override
	public List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd){
		return opMapper.findLinkedOutsourcingsByPrtfCd(prtfCd);
	}
	
	@Override
	public List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd,String query){
		return opMapper.findUnlinkedOutsourcings(prtfCd,entCd,query);
	}
	
	/**
	 * @Transactional: 이 메서드 안의 DB작업이 실패하면, 자동으로 이전 상태로 되돌립니다.
	 * (롤백)
	 */
	
	@Override
	@Transactional
	public void linkOutsourcing(String prtfCd, String osCd, String entCd) {
		//1. 새로운 연결 코드(PK)를 생성합니다.
		String latestOpCd = opMapper.findLatestOpCd();
		int nextNum = 1; // 기본값 1
		if (latestOpCd != null && latestOpCd.startsWith("OP_C")) {
		    try {
		        nextNum = Integer.parseInt(latestOpCd.substring(4)) + 1;
		    } catch (NumberFormatException e) {
		        logger.warn("Failed to parse latestOpCd: {}", latestOpCd, e);
		    }
		}
		String newOpCd = String.format("OP_C%05d", nextNum);
		// 2.매퍼를 호출하면 DB에 연결 정보를 저장합니다.
		opMapper.linkOutsourcingToPortfolio(newOpCd, osCd, prtfCd, entCd);
	}
	
	@Override
	@Transactional
	public void unlinkOutsourcing(String prtfCd, String osCd) {
		opMapper.unlinkOutsourcingFromPortfolio(osCd, prtfCd);
	}
}