package CoffeeShop.SaveLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class ASaveLoader<T> implements ISaveLoader<T> {

    private final String readPath;
    private final String writePath;

    public ASaveLoader(String readPath, String writePath) {
        this.readPath = readPath;
        this.writePath = writePath;
    }

    private List<String> readFile() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(readPath));
        return reader.lines().toList();
    }

    private void writeFile(List<String> data) throws SaveLoaderException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writePath))) {

            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SaveLoaderException(
                "Failed to write file: " + writePath
            );
        }
    }

    abstract T StringToEntity(String str);
    abstract String EntityToString(T entity);

    @Override
    public List<T> LoadData() {
        List<T> data = new ArrayList<>();
        List<String> lines;

		try {
			lines = this.readFile();
		} catch (FileNotFoundException e) {
            throw new SaveLoaderRuntimeException("Failed to read file: '" + readPath + "'");
		}

        for (String line : lines) {
            T item = this.StringToEntity(line);
            if (item != null) {
                data.add(item);
            }
        }

        return data;
    }

    @Override
    public void SaveData(List<T> data) throws SaveLoaderException {
        List<String> lines = new ArrayList<>();

        for (T entity : data) {
            lines.add(this.EntityToString(entity));
        }

        this.writeFile(lines);
    }
}
