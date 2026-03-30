package CoffeeShop.SaveLoader;

import CoffeeShop.Exceptions.SaveLoaderException;

import java.util.List;

public interface ISaveLoader<T> {
    List<T> LoadData();

    void SaveData(List<T> data) throws SaveLoaderException;
}
