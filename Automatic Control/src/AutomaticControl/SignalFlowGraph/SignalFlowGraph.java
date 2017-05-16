package AutomaticControl.SignalFlowGraph;

import java.util.ArrayList;

public class SignalFlowGraph {

	private ArrayList<ArrayList<String>> individualLoops = new ArrayList<>();
	private ArrayList<ArrayList<ArrayList<String>>> twoNonTouching = new ArrayList<>();
	private ArrayList<ArrayList<ArrayList<String>>> threeNonTouching = new ArrayList<>();
	private ArrayList<ArrayList<String>> forwardPaths = new ArrayList<>();
	private ArrayList<Float> gainOfPaths = new ArrayList<>();
	private ArrayList<Float> gainOfLoops = new ArrayList<>();
	private Float D = (float)0;
	public SignalFlowGraph(ArrayList<ArrayList<String>> Loops, ArrayList<ArrayList<String>> paths, ArrayList<Float> pathsGain,
			ArrayList<Float> loopsGain) {
		individualLoops = Loops;
		forwardPaths = paths;
		gainOfLoops = loopsGain;
		gainOfPaths = pathsGain;
	    D = computeDelta(individualLoops);
	}
	public ArrayList<ArrayList<ArrayList<String>>> getnontouchingLoops1(){
		return twoNonTouching;
	}
	public Float getOverallT_F() {
		Float transferFunctionGain = (float) 0;
		Float pathsGain = (float) 0;
		for (int i = 0; i < forwardPaths.size(); i++) {
			Float M = gainOfPaths.get(i);
			Float deltaM = deltaOfPath(forwardPaths.get(i));
			pathsGain = pathsGain + (M * deltaM);
		}
		transferFunctionGain = pathsGain / D;
		return transferFunctionGain;
	}

	public Float computeDelta(ArrayList<ArrayList<String>> individualLoops) {
		Float delta = (float) 1;
		if(individualLoops.size() > 1){
			for (int i = 0; i < individualLoops.size(); i++) {
				for (int j = i+1; j < individualLoops.size(); j++) {
					if (testLoops(individualLoops.get(i), individualLoops.get(j))) {
							ArrayList<ArrayList<String>> twoLoops = new ArrayList<>();
							twoLoops.add(individualLoops.get(i));
							twoLoops.add(individualLoops.get(j));
							twoNonTouching.add(twoLoops);
						}
				}
			}
		     if (twoNonTouching.size() <= 1) {
				delta = 1 - sumIndividualloops(individualLoops) + sumNonTouching(twoNonTouching);
		     } else {
				for (int i = 0; i < twoNonTouching.size(); i++) {
					ArrayList<String> loop_1 = twoNonTouching.get(i).get(0);
					ArrayList<String> loop_2 = twoNonTouching.get(i).get(1);
					ArrayList<ArrayList<String>> threeLoops = new ArrayList<>();
					threeLoops.add(loop_1);
					threeLoops.add(loop_2);
					for (int j = 0; j < individualLoops.size(); j++) {
						if((!loop_1.toString().equals(individualLoops.get(j).toString()))&&(!loop_2.toString().equals(individualLoops.get(j).toString()))){	
							if (testLoops(loop_1, individualLoops.get(j))&&(testLoops(loop_1, individualLoops.get(j)))) {
								if(check1(loop_1,loop_2,individualLoops.get(j))){
									threeLoops.add(individualLoops.get(j));
									if(threeLoops.size()==3){
										threeNonTouching.add(threeLoops);
									}
								}
							}
						}
					}
				}
				delta = 1 - sumIndividualloops(individualLoops) + sumNonTouching(twoNonTouching) - sumNonTouching(threeNonTouching);
			}
		}else {
			delta = 1 - sumIndividualloops(individualLoops);
		}
		twoNonTouching.clear();
		threeNonTouching.clear();
		return delta;
	}

	private boolean check1(ArrayList<String> Loop1,ArrayList<String> Loop2,ArrayList<String> Loop3) {
		// TODO Auto-generated method stub
		for(int i = 0; i < threeNonTouching.size(); i++){
			ArrayList<ArrayList<String>> testthreeLoop = threeNonTouching.get(i);
			if((testthreeLoop.get(0)== Loop1)&&
			   (testthreeLoop.get(1)== Loop2)&&
			   (testthreeLoop.get(2)== Loop3)){
				return false;
			} else if((testthreeLoop.get(0)== Loop1)&&
					   (testthreeLoop.get(1)== Loop3)&&
					   (testthreeLoop.get(2)== Loop2)){
				return false;	
			} else if((testthreeLoop.get(0)== Loop2)&&
					   (testthreeLoop.get(1)== Loop1)&&
					   (testthreeLoop.get(2)== Loop3)){
				return false;		
			} else if((testthreeLoop.get(0)== Loop2)&&
					   (testthreeLoop.get(1)== Loop3)&&
					   (testthreeLoop.get(2)== Loop1)){
				return false;
			} else if((testthreeLoop.get(0)== Loop3)&&
					   (testthreeLoop.get(1)== Loop2)&&
					   (testthreeLoop.get(2)== Loop1)){
				return false;		
			} else if((testthreeLoop.get(0)== Loop3)&&
					   (testthreeLoop.get(1)== Loop1)&&
					   (testthreeLoop.get(2)== Loop2)){
				return false;
			}
		}
		return true;
	}
	private Float sumIndividualloops(ArrayList<ArrayList<String>> individualLoops) {
		Float sum = new Float(0);
		for (int i = 0; i < individualLoops.size(); i++) {
			sum = sum + gainOfLoops.get(i);
		}
		return sum;
	}
	
	private boolean testLoops(ArrayList<String> loop_1, ArrayList<String> loop_2) {
		for (int i = 0; i < loop_1.size(); i++) {
			for (int j = 0; j < loop_2.size(); j++) {
				if (loop_1.get(i) == loop_2.get(j)) {
					return false;
				}
			}
		}
		return true;
	}

	private Float sumNonTouching(ArrayList<ArrayList<ArrayList<String>>> nonloops) {
		Float sum = (float) 0;
		Float gainOfallLoops = (float) 1;
		for (int i = 0; i < nonloops.size(); i++) {
			for (int j = 0; j < nonloops.get(i).size(); j++) {
				for(int x = 0; x < individualLoops.size(); x++){
					if(nonloops.get(i).get(j).equals(individualLoops.get(x))){
						gainOfallLoops = gainOfallLoops * gainOfLoops.get(x);
					}
				}
			}
			sum = sum + gainOfallLoops;
		}
		return sum;
	}
	
	private Float deltaOfPath(ArrayList<String> path) {
		ArrayList<ArrayList<String>> removeLoop = new ArrayList<>();
		ArrayList<Float> removegain = new ArrayList<>();
		Float delta = (float) 1;
		for (int i = 0; i < path.size(); i++) {
			for (int j = individualLoops.size()-1; j >= 0; j--) {
				if (check(individualLoops.get(j),path.get(i))) {
					removeLoop.add(individualLoops.remove(j));
					removegain.add(gainOfLoops.remove(j));
				}
			}
		}
		if (individualLoops.size() > 0) {
			delta = computeDelta(individualLoops);
		}
		for (int x = 0; x < removegain.size(); x++) {
			individualLoops.add(removeLoop.get(x));
			gainOfLoops.add(removegain.get(x));
		}
		return delta;
	}
	
	private boolean check (ArrayList<String> allLoops , String node){
		for(int x = 0; x < allLoops.size(); x++){
			if(allLoops.get(x).equals(node)){
				return true;
			}
		}
		return false;
	}
	public static void main(String[] args) {
		ArrayList<ArrayList<String>>m=new ArrayList<>();
		ArrayList<String> l1 = new ArrayList<>();
		l1.add("1");
		l1.add("2");
		l1.add("3");
		l1.add("4");
		l1.add("5");
		l1.add("6");
		l1.add("7");
		l1.add("8");
		m.add(l1);
		ArrayList<ArrayList<String>>m1=new ArrayList<>();
		ArrayList<String>b = new ArrayList<>();
		ArrayList<String>b1 = new ArrayList<>();
		ArrayList<String>b2 = new ArrayList<>();
		ArrayList<String>b3 = new ArrayList<>();
		b.add("2");b.add("3");b.add("2");
		m1.add(b);
		b1.add("4");b1.add("5");b1.add("4");
		m1.add(b1);
		b2.add("6");b2.add("7");b2.add("6");
		m1.add(b2);
		ArrayList<Float>n=new ArrayList<>();
		n.add((float) 1);
		ArrayList<Float>k=new ArrayList<>();
		k.add((float) -1);
		k.add((float) -1);
		k.add((float) -1);
		SignalFlowGraph x = new SignalFlowGraph(m1, m, n, k);
		System.out.println(x.getOverallT_F());
	}
}
