package util;

public enum RmiVmType {

	LOCAL_LINUX("LOCAL_LINUX"), LOCAL_WINDOWS("LOCAL_WINDOWS"), REMOTE("REMOTE");

	private String value;

	RmiVmType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

}
