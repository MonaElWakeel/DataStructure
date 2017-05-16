package AutomaticControl.SignalFlowGraph;

import java.util.ArrayList;

public class Node {
	private float X;
	private float y;
	private int Name;
	private String name;
	private ArrayList<Edge> INEdges = new ArrayList<>();
	private ArrayList<Edge> OUTEdges = new ArrayList<>();

	public void setName(String name) {
		this.name = name;
	}

	public String GetName() {
		return name;
	}

	public int getName() {
		return Name;
	}

	public void setName(int name) {
		Name = name;
	}

	public ArrayList<Edge> getINEdges() {
		return INEdges;
	}

	public void setINEdges(Edge iNEdges) {
		INEdges.add(iNEdges);
	}

	public ArrayList<Edge> getOUTEdges() {
		return OUTEdges;
	}

	public void setOUTEdges(Edge oUTEdges) {
		OUTEdges.add(oUTEdges);
	}

	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

}
