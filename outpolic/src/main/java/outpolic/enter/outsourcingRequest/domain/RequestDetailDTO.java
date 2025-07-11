package outpolic.enter.outsourcingRequest.domain;

import java.util.List;

public class RequestDetailDTO {
	 private RequestViewDTO request;
	    private List<ReplyDTO> replies;

	    // Getter와 Setter
	    public RequestViewDTO getRequest() { return request; }
	    public void setRequest(RequestViewDTO request) { this.request = request; }
	    public List<ReplyDTO> getReplies() { return replies; }
	    public void setReplies(List<ReplyDTO> replies) { this.replies = replies; }
	}


