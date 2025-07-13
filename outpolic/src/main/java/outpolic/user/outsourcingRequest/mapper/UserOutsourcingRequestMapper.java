package outpolic.user.outsourcingRequest.mapper;

import org.apache.ibatis.annotations.Mapper;
import outpolic.enter.outsourcingRequest.domain.OutsourcingRequestDTO;
import outpolic.enter.outsourcingRequest.domain.RequestViewDTO;
import java.util.List;

@Mapper
public interface UserOutsourcingRequestMapper {
    void insertRequest(OutsourcingRequestDTO request); 
    
    List<RequestViewDTO> findSentRequests(String requesterId); 
    
    RequestViewDTO findRequestDetailById(String requestId); 

    String findLatestOcdCd(); 
}