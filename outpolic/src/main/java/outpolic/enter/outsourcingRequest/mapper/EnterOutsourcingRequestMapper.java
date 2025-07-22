package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;
import java.util.Map; // Map 임포트 추가 

@Mapper
public interface EnterOutsourcingRequestMapper {

    void insertRequest(OutsourcingRequestDTO request); 

    void updateChatRoomId(@Param("ocd_cd") String ocd_cd, @Param("chr_cd") String chr_cd); 
    void updateStatus(@Param("requestId") String requestId, @Param("status") String status); 

    String findLatestOcdCd(); 

    // OutsourcingProgressMapper의 역할 일부를 여기로 이동
    String findLatestOspCd(); // OutsourcingProgress 테이블의 최신 코드 조회 
    void insertInitialProgressStages(@Param("stageList") List<Map<String, Object>> stageList); // 초기 진행 단계 삽입 


    String findEntCdByMbrCd(String mbrCd); 
    String findMbrCdByEntCd(String entCd); 

    RequestViewDTO findRequestDetailById(String requestId); 
    List<RequestViewDTO> findSentRequests(String requesterId); 
    List<RequestViewDTO> findSentInquiries(String requesterId); 
    List<RequestViewDTO> findReceivedRequests(String supplierEntCd); 
    List<RequestViewDTO> findReceivedInquiries(String supplierEntCd); 
}