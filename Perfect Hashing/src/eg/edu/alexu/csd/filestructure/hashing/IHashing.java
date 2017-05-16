package eg.edu.alexu.csd.filestructure.hashing;

import java.io.File;

public interface IHashing {
	public void put(int key, int address);
	public boolean find(int key);
	void readFile(File file);
}
