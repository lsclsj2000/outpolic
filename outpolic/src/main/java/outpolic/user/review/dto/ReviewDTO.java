package outpolic.user.review.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewDTO {
	private String reviewCode; 
	private String outsourcingContractId; 
	private String memberCode; 
	private String reviewContent; 
	private int reviewRating; 
	private LocalDateTime reviewRegYmdt; 
	private LocalDateTime reviewModificationYmdt;
}
