package application;

public class Category {
	private final String label;
	private final String path;
	
	public Category(String label, String path) {
		super();
		this.label = label;
		this.path = path;
	}

	public String getLabel() {
		return label;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return label;
	}
	
	
	
	
}
