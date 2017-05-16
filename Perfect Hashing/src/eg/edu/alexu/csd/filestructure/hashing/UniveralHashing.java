package eg.edu.alexu.csd.filestructure.hashing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class UniveralHashing {
	private int[] hashTable;
	private ArrayList<Integer> keys = new ArrayList<>();
	private String[] hashFunction = null;
	private ArrayList<String> hashTest = new ArrayList<>();

	public String[] getHashFunction() {
		return this.hashFunction;
	}

	public String[] getHashFunction(int b, int u) {
		hashFunction = new String[b];
		for (int i = 0; i < b; i++) {
			String element = new String();
			for (int j = 0; j < u; j++) {
				element = element + String.valueOf((int) (Math.random() * 2));
			}
			hashFunction[i] = element;
		}
		return hashFunction;
	}

	public void put(int key, int address) {
		// TODO Auto-generated method stub
		if (address > hashTable.length) {
			return;
		} else {
			for (int i = 0; i < hashTable.length; i++) {
				if (i == address) {
					hashTable[i] = key;
					hashTest.add("busy");
				}
			}
		}
	}

	public boolean find(int element, int address) {
		// TODO Auto-generated method stub
		if (address > hashTable.length) {
			return false;
		} else {
			for (int i = 0; i < hashTable.length; i++) {
				if (i == address) {
					if (hashTest.get(i).equals("busy")) {
						return true;
					}
				}
			}
		}
		return false;
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

	public void readFile(File file) {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = new String();
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					keys.add(Integer.parseInt(line.trim()));
				}
			}
			SpaceSolutionOfDoubleSize(keys);
		} catch (Exception e) {
		}
	}

	public void SpaceSolutionOfDoubleSize(ArrayList<Integer> keys) {
		int u = 32;
		int b = getNumber((int) Math.pow(keys.size(), 2));
		hashFunction = getHashFunction(b, u);
		for (int i = 0; i < keys.size(); i++) {
			int element = keys.get(i);
			int address = convertBinaryToDecimal(getAddress(hashFunction, String.valueOf(keys.get(i))));
			if (find(element, address)) {
				SpaceSolutionOfDoubleSize(keys);
			} else {
				put(element, address);
			}
		}
	}

	private int getNumber(int size) {

		return (int) (Math.log(size) / Math.log(2));
	}

	public int[] getHashTable() {
		// TODO Auto-generated method stub
		return hashTable;
	}
}
