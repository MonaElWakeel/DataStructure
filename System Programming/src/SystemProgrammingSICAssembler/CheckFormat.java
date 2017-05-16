package SystemProgrammingSICAssembler;

public class CheckFormat {
	private Pass1 pass1 = new Pass1();
	private String lowerCase;
	private int counterBlanck;
	private boolean blanckFlag;
	private boolean labelFlag;
	private boolean operationCodeFlag;
	private boolean operandFlag;
	private String OPend;
	private String OPrsub;

	public CheckFormat() {
		lowerCase = null;
		counterBlanck = 0;
		blanckFlag = false;
		labelFlag = false;
		operationCodeFlag = false;
		operandFlag = false;
		OPend = null;
		OPrsub = null;
	}

	private void checkBlanck(String blanck) {
		blanckFlag = false;
		counterBlanck = 0;
		if (blanck == null) {
			blanckFlag = true;
		} else {
			for (int i = 0; i < blanck.length(); i++) {
				if (Character.isWhitespace(blanck.charAt(i))) {
					counterBlanck++;
				}
			}
			if (counterBlanck == blanck.length()) {
				blanckFlag = true;
			} else {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Blanck is wrong");
				blanckFlag = false;
			}
		}
	}

	private void convertToLowerCase(String str) {
		if (str != null && str.trim().length() > 0) {
			lowerCase = str.toLowerCase();
		} else {
			lowerCase = null;
		}
	}

	private void checkLabel(String labelLowerCase) {
		labelFlag = false;
		if (labelLowerCase == null || labelLowerCase.trim().isEmpty()) {
			labelFlag = true;
		} else {
			String split = labelLowerCase.trim();
			String[] splited = split.split("\\s+");
			if (splited.length > 1) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Label is wrong, Label has space");
				labelFlag = false;
			} else if (splited.length == 1) {
				if (splited[0].contains(".") || splited[0].contains("#") || splited[0].contains("@")) { ///////////////
					Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Label is wrong");
					labelFlag = false;
				} 
				else if (splited[0].equals("start") || splited[0].equals("end") || splited[0].equals("byte")
						|| splited[0].equals("resw") || splited[0].equals("resb") || splited[0].equals("word")) {
					Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Label is wrong");
					labelFlag = false;
				} else if (Character.isLetter(splited[0].charAt(0))) {
					if (pass1.getSymbolTable().containsKey(splited[0])) {
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Label is wrong, Label is duplicate");
						labelFlag = false;
					} else if (pass1.getoperationCode().containsKey(splited[0])) {
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "Label is wrong, Label is keyword");
						labelFlag = false;
					} else {
						labelFlag = true;
						pass1.setLabel(split);
					}
				} else {
					Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "label's first character is not letter");
					labelFlag = false;
				}

			}
		}
	}

	private void checkOperationCode(String operationCodeLowerCase) {
		OPrsub = null;
		OPend = null;
		operationCodeFlag = false;
		if (operationCodeLowerCase == null || operationCodeLowerCase.trim().isEmpty()) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operation code is wrong, operation is empty");
			operationCodeFlag = false;
		} else {
			String split = operationCodeLowerCase.trim();
			String[] splited = split.split("\\s+");
			if (splited.length > 1) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()),
						"operation code is wrong, operation code has space");
				operationCodeFlag = false;
			} else if (splited.length == 1) {
				if (splited[0].equals("start") || splited[0].equals("end") || splited[0].equals("byte")
						|| splited[0].equals("word") || splited[0].equals("resb") || splited[0].equals("resw")) {
					// START,END, BYTE, WORD, RESB, RESW.
					operationCodeFlag = true;
				} else {
					if (pass1.getoperationCode().containsKey(splited[0])) {
						operationCodeFlag = true;
					} else {
						operationCodeFlag = false;
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operationcode is wrong");
					}
				}
				if (splited[0].equals("rsub")) {
					OPrsub = "rsub";
				}
				if (splited[0].equals("end")) {
					OPend = "end";
				}
			}
		}
	}

	private void checkOperand(String operandLowerCase, String operationCode) {
		operandFlag = false;
		String[] splited = null;
		String split = null;
		if (operandLowerCase != null) {
			split = operandLowerCase.trim();
			splited = split.split("\\s+");
		}
		if (OPrsub != null) {
			if (splited == null) {
				operandFlag = true;
			} else {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand is wrong,operand should be empty");
				operandFlag = false;
			}
		} else if (OPend != null) {
			if (splited == null) {
				operandFlag = true;
			} else if (splited.length == 1) {
				operandFlag = true;
				pass1.setOperand(split);

			}

		} else if (splited == null) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand is wrong,operand is empty");
			operandFlag = false;
		} else if (splited.length > 1) {
			////////////////////////////////// word operand
			if (operationCode.equals("word") || operationCode.equals("resw") || operationCode.equals("resb")) {
				operandFlag = checkWordOperand(split, operationCode);
			}
			////////////////////////////////
			else if (operationCode.equals("byte")) {
				operandFlag = checkByteOperand(split);
			} else if (split.contains(",")) {
				check(split.replace(" ", ""), splited, operationCode);
			} else {
				/////////////////////////////////////////// 0x
				if (!checkOX(split, operationCode)) {
					Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
					operandFlag = false;
				} else {
					operandFlag = true;
				}
			}
		} else if (splited.length == 1) {

			if (pass1.getoperationCode().containsKey(splited[0])) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is keyword");
				operandFlag = false;
			} else {
				check(split, splited, operationCode);
			}

		}
	}

	private boolean checkByteOperand(String split) {
		if (!split.startsWith("c") && !split.startsWith("x")) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		char t = split.charAt(0);
		split = split.substring(1).trim();
		if (split.isEmpty()) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		if (t == 'x' && (split.length() <= 2 || !split.startsWith("\'") || !split.endsWith("\'")
				|| !isHexadecimal(split.substring(1, split.length() - 1)))) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		if (t == 'c' && (split.length() <= 2 || !split.startsWith("\'") || !split.endsWith("\'"))) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		if (t == 'c' && (split.length() > 2 && (!split.startsWith("\'") || !split.endsWith("\'")))) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		if (t == 'x' && (split.length() > 2 && (!split.startsWith("\'") || !split.endsWith("\'")
				|| !isHexadecimal(split.substring(1, split.length() - 1))))) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
			return false;
		}
		return true;
	}

	private void check(String split, String[] splited, String operationCode) {
		if (split.contains(String.valueOf(','))) {
			String start = split.substring(0, split.indexOf(","));
			String end = split.substring(split.indexOf(",") + 1, split.length());
			if (start.isEmpty() || end.isEmpty()) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
				operandFlag = false;
			} else if (!end.equals("x")) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand  is wrong");
				operandFlag = false;
			} else {
				if (String.valueOf(start.trim().charAt(0)).equals("0")) {
					start = start.replaceAll(" ", "");
					if (start.length() > 2 && (NOTHEX(start.substring(2), operationCode)
							|| pass1.hexaToDecimal(start.substring(2)) > 32767)) {
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand wrong");
						operandFlag = false;
					}

				} else {
					pass1.setOperand(start.trim());
					operandFlag = true;
				}
			}

		} else {

			if (operationCode.equals("word") || operationCode.equals("resw") || operationCode.equals("resb")) {
				operandFlag = checkWordOperand(split, operationCode);
			} else if (operationCode.equals("byte")) {
				operandFlag = checkByteOperand(split);
			} else {
				if (!operationCode.equals("start")) {
					if (String.valueOf(split.charAt(0)).equals("0") && split.length() > 2
							&& (NOTHEX(split.substring(2), operationCode)
									|| pass1.hexaToDecimal(split.substring(2)) > 32767)) {
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "operand wrong");

					}
					if (!String.valueOf(split.charAt(0)).equals("0")) {
						pass1.setOperand(split.trim());
					}

				}
				operandFlag = true;
			}

		}

	}

	public boolean checkWordOperand(String split, String operationCode) {
		try {
			long y = Long.parseLong(split);
			if (operationCode.equals("word")) {
				if (split.startsWith("-")) {
					y = -1 * y;
					if (y > Pass1.Max_Size) {
						Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "too  large negative number");
						return false;
					}
				} else if (y > Pass1.Max_Size - 1) {
					Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "too large positive number");
					return false;
				}
			}
			return true;
		} catch (NumberFormatException e) {
			return checkOX(split, operationCode);
		}
	}

	public boolean checkOX(String split, String operationCode) {
		System.out.println(operationCode);
		if (!split.contains("x")) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "error operand");
			return false;
		}
		String[] j = split.split("x");
		if (j.length != 2) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "error operand");
			return false;
		}
		if (!j[0].trim().equals("0")) {
			Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "error operand");
			return false;
		}
		if (operationCode.equals("word")) {
			if (NOTHEX(j[1].trim(), operationCode) || j[1].trim().length() > 6) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "error operand");
				return false;
			}
		} else {
			if (NOTHEX(j[1].trim(), operationCode) || pass1.hexaToDecimal(j[1].trim()) > 32767) {
				Pass1.setErrorMessage(String.valueOf(pass1.getBegin()), "error operand");
				return false;
			}
		}
		return true;
	}

	private boolean NOTHEX(String trim, String operationCode) {
		return !isHexadecimal(trim);

	}

	public static boolean isHexadecimal(String value) {
		return value.matches("\\p{XDigit}+");
	}

	public void checkLineFormat(SICline sicLine) {

		// label
		String label = sicLine.getLabel();
		convertToLowerCase(label);
		checkLabel(lowerCase);
		if (labelFlag == false) {
			return;
		}
		// blanck1
		String blanck1 = sicLine.getBlank1();
		checkBlanck(blanck1);
		if (blanckFlag == false) {
			return;
		}
		// operationCode
		String operationCode = sicLine.getOperationCode();
		convertToLowerCase(operationCode);
		checkOperationCode(lowerCase);
		if (operationCodeFlag == false) {
			return;
		}
		// blanck2
		String blanck2 = sicLine.getBlank2();
		checkBlanck(blanck2);
		if (blanckFlag == false) {
			return;
		}
		// operand
		String operand = sicLine.getOperand();
		convertToLowerCase(operand);
		checkOperand(lowerCase, operationCode.toLowerCase().trim());
		if (operandFlag == false) {
			return;
		}

		return;
	}
}
