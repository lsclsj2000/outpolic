package outpolic.user.category.domain;

import java.util.List;

import lombok.Data;

@Data
public class UserCategoryGroup {

	private String id;
	private String name;
	private List<UserSubCategory> subCategorys;
}
