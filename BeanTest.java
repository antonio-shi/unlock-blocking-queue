
public class BeanTest implements Exchengeable{
	private String name = null;
	
	public BeanTest(String name) {
		super();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void exchange(Exchengeable other) {
		this.setName(((BeanTest)other).getName());
	}
	@Override
	public String toString() {
		return "BeanTest [name=" + name + "]";
	}

}
