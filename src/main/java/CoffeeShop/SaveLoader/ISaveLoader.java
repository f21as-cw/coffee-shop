package CoffeeShop.SaveLoader;

import java.util.List;

public interface ISaveLoader<T> {
	List<T> LoadData(String path) throws LoadingException;
	void SaveData(T data);
}
