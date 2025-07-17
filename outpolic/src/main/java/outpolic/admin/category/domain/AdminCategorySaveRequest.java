package outpolic.admin.category.domain;

import lombok.Data;

@Data
public class AdminCategorySaveRequest {

	private String mode;
    private String ctgryId;
    private String ctgryUpId;
    private String ctgryNm;
}
