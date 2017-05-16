package SystemProgrammingSICAssembler;

public class SICline {

	private String label = new String();
	private String blank1 = new String();
	private String operationCode = new String();
	private String blank2 = new String();
	private String operand = new String();
	private String comment = new String();

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getBlank1() {
		return blank1;
	}

	public void setBlank1(String blank1) {
		this.blank1 = blank1;
	}

	public String getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

	public String getBlank2() {
		return blank2;
	}

	public void setBlank2(String blank2) {
		this.blank2 = blank2;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
