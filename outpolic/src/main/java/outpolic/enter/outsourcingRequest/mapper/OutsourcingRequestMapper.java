package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutsourcingRequestMapper {
	String findLatestOcdCd();
	int insertContentListForRequest(String clCd, String ocdCd);
	//int insertOutsourcingRequest(OutsourcingContractDetails contractDetails);
}
