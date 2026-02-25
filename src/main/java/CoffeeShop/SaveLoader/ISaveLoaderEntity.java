
package CoffeeShop.SaveLoader;

import java.io.IOException;
import java.util.List;

public interface ISaveLoaderEntity<T> {
	List<T> LoadData(String path) throws LoadingException;
	void SaveData(List<T> data) throws IOException;
}
