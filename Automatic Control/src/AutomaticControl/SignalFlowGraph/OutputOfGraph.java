package AutomaticControl.SignalFlowGraph;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class OutputOfGraph {
	private JFrame outputFrame;

	public OutputOfGraph(ArrayList<ArrayList<String>> Loops, ArrayList<ArrayList<String>> paths, ArrayList<Float> pathsGain,
			ArrayList<Float> loopsGain,Float transferFunction) {
		outputFrame = new JFrame("signal flow graph");
		outputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		outputFrame.setLocation(20, 20);
		outputFrame.setSize(new Dimension(600, 350));
		JPanel pane1 = new JPanel();
		outputFrame.getContentPane().add(pane1);
		JLabel labe1 = new JLabel("ForwardPaths  ");
		JLabel labe2 = new JLabel("paths gains  ");
		JLabel labe3 = new JLabel("individualloops  ");
		JLabel labe4 = new JLabel("loops gains  ");
		JLabel labe5 = new JLabel("NonTouchingLoops  ");

		JLabel labe6 = new JLabel("overall transfer function  ");
		String title = "Forward Paths: " + paths.get(0).toString();
		for (int i = 1; i < paths.size(); i++) {
			title = title +"&";
			title = title + paths.get(i).toString();
		}
		labe1.setText(title);
		title = "Paths gains: " + pathsGain.get(0);
		for (int i = 1; i < pathsGain.size(); i++) {
			title = title +"&";
			title = title + String.valueOf(pathsGain.get(i));
		}
		labe2.setText(title);
		title = "Loops: " + Loops.get(0).toString();
		for (int i = 1; i < Loops.size(); i++) {
			title = title +"&";
			title = title + Loops.get(i).toString();
		}
		labe3.setText(title);
		title = "Loops gains: " + loopsGain.get(0);
		for (int i = 1; i < loopsGain.size(); i++) {
			title = title +"&";
			title = title + String.valueOf(loopsGain.get(i));
		}
		labe4.setText(title);
		title = "OverAll transfer function : " + transferFunction;
		labe6.setText(title);
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.Y_AXIS));
		pane1.add(labe1);
		pane1.add(labe2);
		pane1.add(labe3);
		pane1.add(labe4);
		pane1.add(labe5);
		pane1.add(labe6);
		outputFrame.setVisible(true);
	}
}
