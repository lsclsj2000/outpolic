package outpolic.enter.op.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import outpolic.enter.outsourcing.domain.EnterOutsourcing;

/**
 * @Mapper: MyBatis가 이 인터페이스를 데이터베이스 통신에 사용하도록 지정합니다.
 */

@Mapper
public interface OpMapper {
	/**
	 * 가장 마지막으로 생성된 외주-포폴 연결 코드(PK)를 조회합니다.
	 * 새로운 코드를 만들기 위해 참고하는 용도입니다.
	 */
	String findLatestOpCd();
	
	/**
	 * 특정 포트폴리오에 이미 연결된 외주 목록을 조회합니다.
	 * @param prtfCd 기준이 되는 포트폴리오의 코드
	 * @return 연결된 외주 목록
	 */
	List<EnterOutsourcing> findLinkedOutsourcingsByPrtfCd(String prtfCd);
	/**
	 * 특정 포트폴리오에 아직 연결되지 않은 외주들을 검색합니다.
	 * @param prtfCd 기준이 되는 포트폴리오 코드
	 * @param entCd 현재 로그인한 기업 코드 (자신이 쓴 외주만 검색 대상)
	 * @param query 사용자가 입력한 검색어
	 * @return 검색된 외주 목록
	 */
	List<EnterOutsourcing> findUnlinkedOutsourcings(@Param("prtfCd") String prtfCd,@Param("entCd") String entCd, @Param("query") String query);
	/**
	 * 포트폴리오와 외주를 연결하는 정보를 outsourcing_portfolio 테이블에 저장(INSERT)합니다.
	 * @Param: 여러 개의 파라미터를 XML에서 사용하기 위해 이름을 지정해주는 어노테이션
	 */
	
	int linkOutsourcingToPortfolio(@Param("opCd") String opCd,@Param("osCd") String osCd,@Param("prtfCd") String prtfCd,@Param("entCd") String entCd);
	/**
	 * 포트폴리오와 외주의 연결을 해체하는 정보를 outsourcing_portfolio 테이블에서 삭제(DELETE) 합니다.
	 */
	int unlinkOutsourcingFromPortfolio(@Param("osCd") String osCd,@Param("prtfCd") String prtfCd);
}