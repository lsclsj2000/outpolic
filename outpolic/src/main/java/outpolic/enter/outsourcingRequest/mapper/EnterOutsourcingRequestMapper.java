package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

@Mapper
public interface EnterOutsourcingRequestMapper {
    void insertRequest(OutsourcingRequestDTO request);
    void updateChatRoomId(@Param("ocd_cd") String ocd_cd, @Param("chr_cd") String chr_cd);
    List<RequestViewDTO> findSentRequests(String requesterId);
    RequestViewDTO findRequestDetailById(String requestId);
    List<RequestViewDTO> findReceivedRequests(String supplierEntCd); // 이 줄이 있어야 함

    String findLatestOcdCd(); // 가장 최근 ocd_cd를 찾는 메서드
	String findEntCdByMbrCd(String mbrCd);
	RequestViewDTO findRequestByOcdCd(String ocdCd);
    
    void updateStatus(@Param("requestId") String requestId, @Param("status") String status);
    
    String findLatestOspCd();
    
    void insertInitialProgress(@Param("ospCd") String ospCd, @Param("ocdCd") String ocdCd, @Param("stcCd") String stcCd);
}