package CoffeeShop.SaveLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class ASaveLoader<T> implements ISaveLoader<T> {
	FileReader _fileReader;
	FileWriter _fileWriter;



	ASaveLoader(String readPath, String writePath) throws IOException {
		this._fileReader = new FileReader(readPath);
		this._fileWriter = new FileWriter(writePath);
	}

	protected List<String> ReadFile() throws LoadingException {

		BufferedReader reader = new BufferedReader(this._fileReader);
		List<String> lines = reader.lines().toList();

		if (lines.size() <= 0) {
			throw new LoadingException("No lines were loaded from file");
		}

		return lines;
	}

	protected void WriteFile(List<String> data) throws IOException {
		BufferedWriter writer = new BufferedWriter(this._fileWriter);


		for (String line : data) {
			writer.write(line);
			writer.newLine();
		}
	}

	abstract T StringToEntity(String str);
	abstract String EntityToString(T entity);

	@Override
	public List<T> LoadData(String path) throws LoadingException  {
		List<T> data = new ArrayList<T>();
		List<String> lines = this.ReadFile();

		for (String line : lines) {
			T item = this.StringToEntity(line);

			if (item != null) {
				data.add(item);
			}
		}

		return data;
	}

	@Override
	public void SaveData(List<T> data) throws IOException {
		List<String> lines = new ArrayList<String>();

		for (T entity : data) {
			String line = this.EntityToString(entity);
			lines.add(line);
		}

		this.WriteFile(lines);
	}
}
