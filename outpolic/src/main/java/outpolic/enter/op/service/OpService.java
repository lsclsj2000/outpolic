package outpolic.enter.op.service;

import java.util.List;

import outpolic.enter.outsourcing.domain.EnterOutsourcing;

/**
 * '외주-포트폴리오 연결' 관련 비즈니스 로직의 설계도
 */
public interface OpService {
	// 연결된 외주 목록 가져오기
	List<EnterOutsourcing> getLinkedOutsourcings(String prtfCd);
	// 연결 안 된 외주 검색하기
	List<EnterOutsourcing> searchUnlinkedOutsourcings(String prtfCd, String entCd, String query);
	// 외주 연결하기
	void linkOutsourcing(String prtfCd, String osCd, String entCd);
	// 외주 연결 해체하기
	void unlinkOutsourcing(String prtfCd, String osCd);

}
