package CoffeeShop.SaveLoader;

import java.util.List;

public interface ISaveLoader<T> {
	List<T> LoadData(String path);
	void SaveData(T data);
}
