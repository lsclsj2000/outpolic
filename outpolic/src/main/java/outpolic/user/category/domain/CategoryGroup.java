package outpolic.user.category.domain;

import java.util.List;

import lombok.Data;

@Data
public class CategoryGroup {

	private String id;
	private String name;
	private List<SubCategory> subCategorys;
}
