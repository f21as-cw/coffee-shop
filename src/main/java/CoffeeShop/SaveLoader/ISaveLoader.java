package CoffeeShop.SaveLoader;

import java.util.List;

import CoffeeShop.Exceptions.SaveLoaderException;

public interface ISaveLoader<T> {
	List<T> LoadData();
	void SaveData(List<T> data) throws SaveLoaderException;
}
