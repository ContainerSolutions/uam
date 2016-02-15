package commons;

public interface Store {

	void put(String key, String value);

	String get(String key);

	String remove(String key);
}
