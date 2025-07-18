package outpolic.admin.category.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AdminCategory {

	private String ctgryId;
	private String admCd;
	private String ctgryNm;
	private String ctgryUpId;
	private LocalDateTime ctgryRegYmdt;
	private LocalDateTime ctgryMdfcnYmdt;
	private String ctgryAdmCd;
	
	private transient List<AdminCategory> children = new ArrayList<>();
}
