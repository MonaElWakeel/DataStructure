package AutomaticControl.SignalFlowGraph;

public class Edge {
		private Node toNode;
		private Node fromNode;
		private float Gain;

		public Node getToNode() {
			return toNode;
		}

		public void setToNode(Node toNode) {
			this.toNode = toNode;
		}

		public Node getFromNode() {
			return fromNode;
		}

		public void setFromNode(Node fromNode) {
			this.fromNode = fromNode;
		}

		public float getGain() {
			return Gain;
		}

		public void setGain(float gain) {
			Gain = gain;
		}
}
