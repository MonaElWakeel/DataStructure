package AutomaticControl.SignalFlowGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlowGraph {
	private int NumberOfNodes;
	private int action;
	private float initialX = 20;
	private float initialY = 150;
	private ArrayList<String> path = new ArrayList<>();
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	private ArrayList<String> forwardPaths = new ArrayList<>();
	private ArrayList<String> Loops = new ArrayList<>();
	private ArrayList<Float> GAINPath = new ArrayList<>();
	private ArrayList<Float> GAINloop = new ArrayList<>();
	private ArrayList<ArrayList<String>> forwardPath = new ArrayList<>();
	private ArrayList<ArrayList<String>> Loop = new ArrayList<>();
	private JFrame frame1, frame2;

	private void CreateGUI() {
		frame1 = new JFrame("signal flow grapgh");
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.setLocation(100, 100);
		frame1.setSize(new Dimension(500, 500));
		JPanel pane1 = new JPanel(new GridBagLayout());
		frame1.getContentPane().add(pane1);
		GridBagConstraints w = new GridBagConstraints();

		JLabel label = new JLabel("enter number of nodes");
		w.gridx = 0;
		w.gridy = 0;
		w.insets = new Insets(20, 20, 20, 20);
		pane1.add(label, w);
		final JTextField textField = new JTextField("Enter Number");
		w.gridx = 0;
		w.gridy = 1;
		pane1.add(textField, w);
		JButton enter = new JButton("enter");
		w.gridx = 0;
		w.gridy = 2;
		pane1.add(enter, w);
		textField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				textField.setText("");
			}
		});
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textField.getText().equals("Enter Number")) {
					NumberOfNodes = Integer.parseInt(textField.getText());
					action = 1;
					for (int i = 0; i < NumberOfNodes; i++) {
						Node node = new Node();
						node.setName(i + 1);
						node.setX(initialX);
						node.setY(initialY);
						initialX = initialX + 80;
						nodes.add(node);
					}
					frame2 = new JFrame("signal flow graph");
					frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					JPanel pane2 = new JPanel(new GridBagLayout());
					JPanel pane3 = new JPanel(new GridBagLayout()) {
						@Override
						public void paintComponent(Graphics g) {
							super.paintComponent(g);
							if (action == 1) {
								draw(g);
							} else if (action == 2) {
								draw(g);

							}

						}

						private void draw(Graphics g) {
							int i = 0;
							while (i < nodes.size()) {

								g.setColor(Color.red);
								g.fillOval((int) nodes.get(i).getX(), (int) nodes.get(i).getY(), 30, 30);
								g.setColor(Color.black);
								g.drawString(String.valueOf(i + 1), (int) nodes.get(i).getX() + 15,
										(int) nodes.get(i).getY() + 15);
								i++;
							}
							i = 0;
							while (i < edges.size()) {
								if (edges.get(i).getToNode().getName() - edges.get(i).getFromNode().getName() == 1) {
									g.setColor(Color.blue);
									g.drawLine((int) edges.get(i).getToNode().getX() + 15,
											(int) edges.get(i).getToNode().getY() + 15,
											(int) edges.get(i).getFromNode().getX() + 15,
											(int) edges.get(i).getFromNode().getY() + 15);
									int[] xPoints = new int[3];
									int[] yPoints = new int[3];
									xPoints[0] = (int) edges.get(i).getToNode().getX() + 15;
									xPoints[1] = (int) edges.get(i).getToNode().getX() - 10;
									xPoints[2] = (int) edges.get(i).getToNode().getX() - 10;
									yPoints[0] = (int) edges.get(i).getToNode().getY() + 15;
									yPoints[1] = (int) edges.get(i).getToNode().getY() + 20;
									yPoints[2] = (int) edges.get(i).getToNode().getY() + 10;

									g.fillPolygon(xPoints, yPoints, 3);
									g.drawString(String.valueOf(edges.get(i).getGain()),
											(int) edges.get(i).getToNode().getX() - 40,
											(int) edges.get(i).getToNode().getY() + 10);
								} else if (edges.get(i).getToNode().getName() > edges.get(i).getFromNode().getName()) {
									g.setColor(Color.DARK_GRAY);
									g.drawArc((int) edges.get(i).getFromNode().getX() + 15, 100,
											(int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX(),
											100, 0, 180);
									int[] xPoints = new int[3];
									int[] yPoints = new int[3];
									xPoints[0] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 40;
									xPoints[1] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 15;
									xPoints[2] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 15;
									yPoints[0] = 100;
									yPoints[1] = 105;
									yPoints[2] = 95;

									g.fillPolygon(xPoints, yPoints, 3);
									g.drawString(
											String.valueOf(
													edges.get(i).getGain()),
											(int) edges.get(i).getFromNode().getX()
													+ (((int) edges.get(i).getToNode().getX()
															- (int) edges.get(i).getFromNode().getX()) / 2)
													+ 40,
											90);

								} else if (edges.get(i).getToNode().getName() < edges.get(i).getFromNode().getName()) {
									g.setColor(Color.black);
									g.drawArc((int) edges.get(i).getToNode().getX() + 15, 120,
											(int) edges.get(i).getFromNode().getX()
													- (int) edges.get(i).getToNode().getX(),
											100, 180, 180);
									int[] xPoints = new int[3];
									int[] yPoints = new int[3];
									xPoints[0] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 15;
									xPoints[1] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 40;
									xPoints[2] = (int) edges.get(i).getFromNode().getX()
											+ (((int) edges.get(i).getToNode().getX()
													- (int) edges.get(i).getFromNode().getX()) / 2)
											+ 40;
									yPoints[0] = 220;
									yPoints[1] = 225;
									yPoints[2] = 215;

									g.fillPolygon(xPoints, yPoints, 3);
									g.drawString(
											String.valueOf(
													edges.get(i).getGain()),
											(int) edges.get(i).getFromNode().getX()
													+ (((int) edges.get(i).getToNode().getX()
															- (int) edges.get(i).getFromNode().getX()) / 2)
													+ 15,
											230);

								} else {
									g.setColor(Color.green);
									g.drawOval((int) edges.get(i).getToNode().getX() + 15,
											(int) edges.get(i).getToNode().getY() + 15, 30, 30);
									int[] xPoints = new int[3];
									int[] yPoints = new int[3];
									xPoints[0] = (int) edges.get(i).getToNode().getX() + 30;
									xPoints[1] = (int) edges.get(i).getToNode().getX() + 15;
									xPoints[2] = (int) edges.get(i).getToNode().getX() + 15;
									yPoints[0] = (int) edges.get(i).getToNode().getY() + 20;
									yPoints[1] = (int) edges.get(i).getToNode().getY() + 15;
									yPoints[2] = (int) edges.get(i).getToNode().getY() + 20;

									g.fillPolygon(xPoints, yPoints, 3);
									g.drawString(String.valueOf(edges.get(i).getGain()),
											(int) edges.get(i).getToNode().getX() + 30,
											(int) edges.get(i).getToNode().getY() + 5);

								}

								i++;
							}

						}

					};

					frame2.setLocation(200, 20);
					frame2.setSize(new Dimension(700, 700));
					frame2.getContentPane().add(pane2, BorderLayout.NORTH);
					frame2.getContentPane().add(pane3, BorderLayout.CENTER);
					frame2.repaint();
					GridBagConstraints c = new GridBagConstraints();
					JLabel label2 = new JLabel("from node");
					c.gridx = 0;
					c.gridy = 0;
					c.insets = new Insets(20, 20, 20, 20);
					pane2.add(label2, c);
					JLabel label3 = new JLabel("to node");
					c.gridx = 1;
					c.gridy = 0;
					pane2.add(label3, c);
					JLabel label4 = new JLabel("gain");
					c.gridx = 2;
					c.gridy = 0;
					pane2.add(label4, c);
					final JTextField textField1 = new JTextField("Enter Node");
					c.gridx = 0;
					c.gridy = 1;
					pane2.add(textField1, c);
					final JTextField textField2 = new JTextField("Enter Node");
					c.gridx = 1;
					c.gridy = 1;
					pane2.add(textField2, c);
					final JTextField textField3 = new JTextField("Enter gain");
					c.gridx = 2;
					c.gridy = 1;
					pane2.add(textField3, c);
					textField1.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							textField1.setText("");
						}
					});
					textField2.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							textField2.setText("");
						}
					});
					textField3.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							textField3.setText("");
						}
					});
					JButton enter1 = new JButton("next Node");
					c.gridx = 3;
					c.gridy = 0;
					pane2.add(enter1, c);
					JButton enter2 = new JButton("DONE");
					c.gridx = 4;
					c.gridy = 0;
					pane2.add(enter2, c);
					JLabel label5 = new JLabel("from node");
					c.gridx = 0;
					c.gridy = 4;
					pane2.add(label5, c);
					JLabel label6 = new JLabel("to node");
					c.gridx = 1;
					c.gridy = 4;
					pane2.add(label6, c);
					final JTextField textField4 = new JTextField("Enter Node");
					c.gridx = 0;
					c.gridy = 5;
					pane2.add(textField4, c);
					final JTextField textField5 = new JTextField("Enter Node");
					c.gridx = 1;
					c.gridy = 5;
					pane2.add(textField5, c);
					textField4.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							textField4.setText("");
						}
					});
					textField5.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							textField5.setText("");
						}
					});
					JButton enter3 = new JButton("Clear EDGE");
					c.gridx = 2;
					c.gridy = 4;
					pane2.add(enter3, c);
					enter1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Edge edge = new Edge();
							edge.setFromNode(nodes.get(Integer.parseInt(textField1.getText()) - 1));
							edge.setToNode(nodes.get(Integer.parseInt(textField2.getText()) - 1));
							edge.setGain(Float.parseFloat(textField3.getText()));
							nodes.get(Integer.parseInt(textField1.getText()) - 1).setOUTEdges(edge);
							nodes.get(Integer.parseInt(textField2.getText()) - 1).setINEdges(edge);
							edges.add(edge);
							textField1.setText("");
							textField2.setText("");
							textField3.setText("");
							action = 2;
							frame2.repaint();

						}
					});
					enter2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							getResult();

						}

						private void getResult() {

							for (int i = 0; i < nodes.size(); i++) {
								nodes.get(i).setName(String.valueOf(nodes.get(i).getName()));

							}
							find(nodes.get(0));
							for (int i = 0; i < nodes.size(); i++) {
								path = new ArrayList<String>();
								findLoops(nodes.get(i));
							}
							getGain();

							/*
							 * Printing
							 */
							System.out.println("forward Paths:");
							System.out.println(forwardPath);
							System.out.println("Loops:");
							System.out.println(Loop);
							// for(int j=0;j< forwardPath.size();j++){
							// for (int i = 0; i < forwardPath.get(j).size();
							// i++) {
							// System.out.println(forwardPath.get(j).get(i));
							// }
							// }
							System.out.println("--------------------------------------");
							System.out.println("forward Paths Gains:");
							for (int i = 0; i < GAINPath.size(); i++) {
								System.out.println(GAINPath.get(i));
							}
							System.out.println("--------------------------------------");
							System.out.println("individual Loops Gains:");
							for (int i = 0; i < GAINloop.size(); i++) {
								System.out.println(GAINloop.get(i));
							}
							SignalFlowGraph p = new SignalFlowGraph(Loop,forwardPath, GAINPath, GAINloop);
							OutputOfGraph test = new OutputOfGraph(Loop,forwardPath, GAINPath, GAINloop,p.getOverallT_F());
						}
					});
					enter3.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {

							Edge edge = searchEdge(textField5.getText(), textField4.getText());
							if (edge != null) {
								edges.remove(edge);
								nodes.get(Integer.parseInt(textField4.getText()) - 1).getOUTEdges().remove(edge);
								nodes.get(Integer.parseInt(textField5.getText()) - 1).getINEdges().remove(edge);
								frame2.repaint();
							}

						}
					});
					frame2.setVisible(true);
					frame1.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(frame1, "Please enter correct number");
				}
			}
		});
		frame1.setVisible(true);

	}

	private Float GetEdge(Node from, Node to) {
		for (int i = 0; i < edges.size(); i++) {

			if (edges.get(i).getFromNode().equals(from) && edges.get(i).getToNode().equals(to)) {
				return edges.get(i).getGain();
			}
		}

		return null;
	}

	private Node getNode(String valueOf) {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).GetName().equals(valueOf)) {
				return nodes.get(i);
			}
		}

		return null;
	}

	public void getGain() {
		float t = 1;
		for (int i = 0; i < forwardPath.size(); i++) {
			for (int j = 0; j < forwardPath.get(i).size() - 1; j++) {
				Node from = getNode(String.valueOf(forwardPath.get(i).get(j)));
				Node to = getNode(String.valueOf(forwardPath.get(i).get(j + 1)));
				t = t * GetEdge(from, to);

			}
			GAINPath.add(t);
			t = 1;
		}
		t = 1;
		for (int i = 0; i < Loop.size(); i++) {
			for (int j = 0; j < Loop.get(i).size() - 1; j++) {
				Node from = getNode(String.valueOf(Loop.get(i).get(j)));
				Node to = getNode(String.valueOf(Loop.get(i).get(j + 1)));
				t = t * GetEdge(from, to);
			}

			GAINloop.add(t);
			t = 1;
		}

	}

	private boolean touchingNode(ArrayList<String> path, Node node) {

		if (!path.isEmpty() && node.GetName().equals(path.get(0))) {
			return true;
		}

		return false;
	}

	private void find(Node node) {
		int i = 0;
		if (touchingNodes(path, node) == true) {
			return;
		} else if (node.getName() == nodes.size()) {
			path.add(node.GetName());
			forwardPath.add((ArrayList<String>) path.clone());
			path.remove(path.size() - 1);
			return;
		} else {
			while (i < node.getOUTEdges().size()) {
				if (i == 0) {
					path.add(node.GetName());
				}
				find(node.getOUTEdges().get(i).getToNode());
				i++;
			}
			path.remove(path.size() - 1);
		}
	}

	private void findLoops(Node node) {
		int i = 0;
		if (node.getOUTEdges().size() == 0) {
			return;
		}
		if (touchingNode(path, node) == false && touchingNodes(path, node) == true) {
			return;
		} else if (touchingNode(path, node) == true) {
			path.add(node.GetName());
			if (check((ArrayList<String>) path.clone()) == false) {
				Loop.add((ArrayList<String>) path.clone());
			}
			path.remove(path.size() - 1);
			return;
		} else {
			while (i < node.getOUTEdges().size()) {
				if (i == 0) {
					path.add(node.GetName());
				}
				findLoops(node.getOUTEdges().get(i).getToNode());
				i++;
			}
			if (!path.isEmpty()) {
				path.remove(path.size() - 1);
			}

		}

	}

	private boolean check(ArrayList<String> path2) {
		ArrayList<ArrayList<String>> b = (ArrayList<ArrayList<String>>) Loop.clone();
		for (int i = 0; i < Loop.size(); i++) {
			if (path2.size() == Loop.get(i).size()) {
				String h = path2.remove(path2.size() - 1);
				String g = b.get(i).remove(b.get(i).size() - 1);
				if (hasSameChar(path2, b.get(i)) == true) {
					b.get(i).add(g);
					path2.add(h);
					return true;
				}
				b.get(i).add(g);
				path2.add(h);
			}
		}
		return false;
	}

	private Edge searchEdge(String toNode, String fromNode) {
		for (int i = 0; i < edges.size(); i++) {
			if (edges.get(i).getFromNode() == nodes.get(Integer.parseInt(fromNode) - 1)
					&& edges.get(i).getToNode() == nodes.get(Integer.parseInt(toNode) - 1)) {
				return edges.get(i);
			}
		}
		return null;
	}

	private boolean touchingNodes(ArrayList<String> path, Node node) {
		for (int i = 0; i < path.size(); i++) {
			if (node.GetName().equals(path.get(i))) {
				return true;
			}
		}
		return false;
	}

	private boolean hasSameChar(ArrayList<String> str1, ArrayList<String> str2) {
		boolean v = false;
		for (int i = 0; i < str1.size(); i++) {
			v = false;
			for (int j = 0; j < str2.size(); j++) {
				if (str1.get(i).equals(str2.get(j))) {
					v = true;
				}
			}
			if (v == false) {
				return false;

			}
		}
		return true;
	}

	public static void main(String[] args) {
		FlowGraph v = new FlowGraph();
		v.CreateGUI();

	}
}