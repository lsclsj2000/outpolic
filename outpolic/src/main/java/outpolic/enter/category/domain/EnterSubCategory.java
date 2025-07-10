package outpolic.enter.category.domain;

import lombok.Data;

@Data
public class EnterSubCategory {

	private String id;
	private String name;
	private String url;
	
	public EnterSubCategory (String id, String name) {
		this.id = id;
		this.name = name;
		this.url = "/enter/products?category=" + id;
	}
	
}
