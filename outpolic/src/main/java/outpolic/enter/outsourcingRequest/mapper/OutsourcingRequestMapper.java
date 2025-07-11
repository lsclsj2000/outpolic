package outpolic.enter.outsourcingRequest.mapper;



import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.ReplyDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;


@Mapper
public interface OutsourcingRequestMapper {
	List<Map<String,Object>> searchEnterprises(String query);
	void insertRequest(OutsourcingRequestDTO request);
	 List<RequestViewDTO> findAllRequestsByUserId(String userId);
	    RequestViewDTO findRequestDetailById(String requestId);
	    void insertReply(ReplyDTO replyDto);
	    List<ReplyDTO> findRepliesByRequestId(String requestId);

}
