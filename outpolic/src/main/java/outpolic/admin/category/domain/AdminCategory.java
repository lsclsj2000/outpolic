package outpolic.admin.category.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AdminCategory {

	private String ctgryId;
	private String ctgryNm;
	private String ctgryUpId;
	
	private transient List<AdminCategory> children = new ArrayList<>();
}
