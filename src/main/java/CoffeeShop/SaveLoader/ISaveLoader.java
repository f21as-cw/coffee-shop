package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;

public interface ISaveLoader<T> {
	List<T> LoadData();
	void SaveData(List<T> data) throws SaveLoaderException;
}
