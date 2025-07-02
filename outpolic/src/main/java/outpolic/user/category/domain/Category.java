package outpolic.user.category.domain;

import lombok.Data;

@Data
public class Category {
	
	private String categoryId;
	private String categoryName;
	private String parentCategoryId;
}
