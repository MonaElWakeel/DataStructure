package eg.edu.alexu.csd.filestructure.hashing;

public class Element {
	 private int[]elements;
	 private String[]hashFunction=null;
	 
	 
	public String[] getHashFunction() {
		return hashFunction;
	}
	 
	public void setHashFunction(String[] hashFunction) {
		this.hashFunction = hashFunction;
	}
	 
	public int[] getElements() {
		return elements;
	}
	 
	public void setElements(int[] elements) {
		this.elements = elements;
	}
	}
	 