package outpolic.enter.category.domain;

import java.util.List;

import lombok.Data;

@Data
public class EnterCategoryGroup {

	private String id;
	private String name;
	private List<EnterSubCategory> subCategorys;
}
