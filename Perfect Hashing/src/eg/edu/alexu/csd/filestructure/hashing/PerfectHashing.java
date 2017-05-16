package eg.edu.alexu.csd.filestructure.hashing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap; 

public class PerfectHashing implements IHashing {
	private Element[] hashTable;
	private String[] hashFunction = null;

	public void put(int key, int address) {
		int[] element = new int[1];
		element[0] = key;
		Element e = new Element();
		e.setElements(element);
		this.hashTable[address] = e;
	}

	@Override
	public boolean find(int key) {
		if (hashFunction == null) {
			return false;
		}
		String element = convertDecimalToBinary(String.valueOf(key));
		String address1 = getAddress(hashFunction, element);
		int address2 = 0;
		if (this.getHashTable()[Integer.parseInt(address1)].getHashFunction() != null) {
			address2 = convertBinaryToDecimal(
					getAddress(this.getHashTable()[Integer.parseInt(address1)].getHashFunction(), element));
		}
		int elem = this.getHashTable()[Integer.parseInt(address1)].getElements()[address2];

		return (elem != 0);
	}

	public Element[] getHashTable() {
		return hashTable;
	}

	public String[] getHashFunction(int b, int u) {
		String[] hashFunction = new String[b];
		for (int i = 0; i < b; i++) {
			String element = new String();
			for (int j = 0; j < u; j++) {
				element = element + String.valueOf((int) (Math.random() * 2));
			}
			hashFunction[i] = element;
		}
		return hashFunction;
	}

	private String convertDecimalToBinary(String decimal) {
		return Integer.toString(Integer.parseInt(decimal, 10), 2);
	}

	private int convertBinaryToDecimal(String binary) {
		return Integer.parseInt(binary, 2);
	}

	private String getZero(String decimalToHexa) {
		while (decimalToHexa.length() < 32) {
			decimalToHexa = "0" + decimalToHexa;
		}
		return decimalToHexa;

	}

	public String getAddress(String[] hashFunction, String keys) {
		keys = getZero(keys);
		String resultOfrow = new String();
		int mul = 1;
		for (int i = 0; i < hashFunction.length; i++) {
			int sum = 0;
			for (int j = 0; j < keys.length(); j++) {
				int numOfHash = Integer.parseInt(String.valueOf(hashFunction[i].charAt(j)));
				int keyNumber = Integer.parseInt(String.valueOf(keys.charAt(j)));
				mul = numOfHash * keyNumber;
				sum = sum + mul;
			}
			if (sum != 1 && sum != 0) {
				sum = sum % 2;
			}
			resultOfrow = resultOfrow + String.valueOf(sum);
		}
		return resultOfrow;
	}

	@Override
	public void readFile(File file) {
		ArrayList<Integer> elements = new ArrayList<>();
		try {
			BufferedReader words = new BufferedReader(new FileReader(file));
			for (String line; (line = words.readLine()) != null;) {
				if (!line.trim().isEmpty()) {
					elements.add(Integer.parseInt(line.trim()));
				}
			}
			SpaceSolutionOfOneSize(elements);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void SpaceSolutionOfOneSize(ArrayList<Integer> elements) {
		hashTable = new Element[elements.size()];
		int b = getNumber(elements.size());
		hashFunction = getHashFunction(b, 32);
		ArrayList<ArrayList<Integer>> collisions = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			String key = convertDecimalToBinary(String.valueOf(elements.get(i)));
			String address = getAddress(hashFunction, key);
			if (this.getHashTable()[Integer.parseInt(address)].getElements().length != 1
					|| this.getHashTable()[Integer.parseInt(address)].getElements()[1] != 0) {
				this.put(elements.get(i), Integer.parseInt(address));

			} else {
				collisions = insert(Integer.parseInt(address), elements.get(i), collisions);
			}

		}
		for (int i = 0; i < collisions.size(); i++) {
			UniveralHashing hash = new UniveralHashing();
			int address = collisions.get(i).remove(0);
			hash.SpaceSolutionOfDoubleSize(collisions.get(i));
			int[] element = hash.getHashTable();
			Element e = new Element();
			e.setElements(element);
			e.setHashFunction(hash.getHashFunction());
			this.hashTable[address] = e;
		}
	}

	private int getNumber(int size) {

		return (int) (Math.log(size) / Math.log(2));
	}

	private ArrayList<ArrayList<Integer>> insert(int address, int element, ArrayList<ArrayList<Integer>> collisions) {
		boolean find = false;
		int i = 0;
		for (i = 0; i < collisions.size(); i++) {
			if (collisions.get(i).get(0).equals(address)) {
				find = true;
				break;
			}
		}
		if (find == true) {
			collisions.get(i).add(element);
		} else {
			ArrayList<Integer> temp = new ArrayList<>();
			temp.add(address);
			temp.add(element);
			collisions.add(temp);
		}
		return collisions;
	}

}