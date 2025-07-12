package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

@Mapper
public interface OutsourcingRequestMapper {
    void insertRequest(OutsourcingRequestDTO request);
    void updateChatRoomId(@Param("ocd_cd") String ocd_cd, @Param("chr_cd") String chr_cd);
    List<RequestViewDTO> findSentRequests(String requesterId);
    RequestViewDTO findRequestDetailById(String requestId);

    // 추가: 가장 최근 ocd_cd를 찾는 메서드
    String findLatestOcdCd();
    
    List<RequestViewDTO> findReceivedRequests(String supplierEntCd);

}