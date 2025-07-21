package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;
import java.util.Map;

@Mapper
public interface EnterOutsourcingRequestMapper {

    void insertRequest(OutsourcingRequestDTO request);

    void updateChatRoomId(@Param("ocd_cd") String ocd_cd, @Param("chr_cd") String chr_cd);

    void updateStatus(@Param("requestId") String requestId, @Param("status") String status);

    String findLatestOcdCd();

    String findLatestOspCd();

    String findEntCdByMbrCd(String mbrCd);
    
    // [추가] 기업 코드로 회원 코드를 조회하는 메서드 선언
    String findMbrCdByEntCd(String entCd);

    RequestViewDTO findRequestDetailById(String requestId);

    List<RequestViewDTO> findSentRequests(String requesterId);
    
    // [추가] 보낸 '문의' 목록을 조회하는 메서드 선언
    List<RequestViewDTO> findSentInquiries(String requesterId);

    List<RequestViewDTO> findReceivedRequests(String supplierEntCd);

    List<RequestViewDTO> findReceivedInquiries(String supplierEntCd);

    void insertInitialProgressStages(@Param("stageList") List<Map<String, Object>> stageList);
}