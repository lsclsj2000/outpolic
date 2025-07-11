package outpolic.enter.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.ReplyDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

@Mapper
public interface OutsourcingRequestMapper {
    void insertRequest(OutsourcingRequestDTO request);
    List<RequestViewDTO> findSentOutsourcingRequests(String requesterId);
    RequestViewDTO findRequestDetailById(String requestId);
    List<ReplyDTO> findRepliesByRequestId(String requestId);
    void insertReply(ReplyDTO replyDto);
    int updateRequestStatus(@Param("requestId") String requestId, @Param("statusCd") String statusCd);
    String findProviderMbrCdByEntCd(String entCd);
}