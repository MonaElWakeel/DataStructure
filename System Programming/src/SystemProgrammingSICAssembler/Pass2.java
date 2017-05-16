package SystemProgrammingSICAssembler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Pass2 {
	private static LinkedHashMap<String, String> objectCode = new LinkedHashMap<>();
	private ArrayList<String> objectProgram = new ArrayList<>();
	private Pass1 pass = new Pass1();
	ArrayList<String> progrmLines = new ArrayList<>();

	public Pass2(Pass1 pass) {
		this.pass = pass;
	}

	private LinkedHashMap<String, String> AsciiTable = makeMap();

	private LinkedHashMap<String, String> makeMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		String Letters = "abcdefghijklmnopqrstuvwxyz";
		String characters1 = " " + "!" + "\"" + "#" + "$" + "%" + "&" + "\'" + "(" + ")" + "*" + "+" + "," + "-" + "."
				+ "/";
		String characters2 = ":" + ";" + "<" + "=" + ">" + "?" + "@";
		String characters3 = "[" + "\\" + "]" + "^" + "_" + "`";
		String characters4 = "{" + "|" + "}" + "~";
		String upperLetters = Letters.toUpperCase();
		int index = 65;
		for (int i = 0; i < upperLetters.length(); i++) {
			String stringIndex = pass.decimalToHexa(index);
			result.put(Character.toString(upperLetters.charAt(i)), stringIndex);
			index++;
		}
		int index2 = 97;
		for (int i = 0; i < Letters.length(); i++) {
			String stringIndex2 = pass.decimalToHexa(index2);
			result.put(Character.toString(Letters.charAt(i)), stringIndex2);
			index2++;
		}
		int index3 = 48;
		for (int i = 0; i < 10; i++) {
			String stringIndex3 = pass.decimalToHexa(index3);
			result.put(String.valueOf(i), stringIndex3);
			index3++;
		}
		int index4 = 32;
		for (int i = 0; i < characters1.length(); i++) {
			String stringIndex4 = pass.decimalToHexa(index4);
			result.put(Character.toString(characters1.charAt(i)), stringIndex4);
			index4++;
		}
		int index5 = 58;
		for (int i = 0; i < characters2.length(); i++) {
			String stringIndex5 = pass.decimalToHexa(index5);
			result.put(Character.toString(characters2.charAt(i)), stringIndex5);
			index5++;
		}
		int index6 = 91;
		for (int i = 0; i < characters3.length(); i++) {
			String stringIndex6 = pass.decimalToHexa(index6);
			result.put(Character.toString(characters3.charAt(i)), stringIndex6);
			index6++;
		}
		int index7 = 123;
		for (int i = 0; i < characters4.length(); i++) {
			String stringIndex7 = pass.decimalToHexa(index7);
			result.put(Character.toString(characters4.charAt(i)), stringIndex7);
			index7++;
		}
		return result;
	}

	public void load() {
		getOpcode();
		int i = 0;
		String startAddress = new String();
		String endAddress = new String();
		String code = new String();
		int length = 0;
		if (pass != null) {
			while (i < pass.getprogram().size()) {
				if (i == 0 && pass.getprogram().get(i).getOperationCode().trim().toLowerCase().equals("start")) {

					objectProgram.add("H" + "^" + pass.getprogram().get(i).getLabel().trim()
							+ getSpaces(6 - pass.getprogram().get(i).getLabel().trim().length()) + "^"
							+ getZeros(pass.getaddresses().get(i)) + "^"
							+ getZeros(pass.decimalToHexa(pass.hexaToDecimal(pass.LengthOfEnd)
									- pass.hexaToDecimal(pass.getaddresses().get(i)))));
				} else {
					if (i == pass.getprogram().size() - 1
							&& pass.getprogram().get(i).getOperationCode().trim().toLowerCase().equals("end")) {
						if (pass.getprogram().get(i).getOperand() == null
								|| pass.getprogram().get(i).getOperand().trim().isEmpty()
								|| pass.getprogram().get(i).getOperand().equals(new String())) {
							objectProgram.add("E" + "^" + getZeros(pass.startAdress));
						} else {
							objectProgram.add("E" + "^" + getZeros(pass.getSymbolTable()
									.get(pass.getprogram().get(i).getOperand().trim().toLowerCase())));
						}

					} else {
						startAddress = pass.getaddresses().get(i);
						while (i != pass.getprogram().size() - 1 && length < 30) {// end
							endAddress = pass.getaddresses().get(i);
							length = pass.hexaToDecimal(endAddress) - pass.hexaToDecimal(startAddress);
							if (objectCode.get(endAddress) == null) {
								while (i != pass.getprogram().size() - 1 && (pass.getprogram().get(i).getOperationCode()
										.trim().toLowerCase().equals("resb")
										|| pass.getprogram().get(i).getOperationCode().trim().toLowerCase()
												.equals("resw"))) {
									i++;
								}
								break;
							}
							if (!valid(startAddress, i)) {
								break;
							}
							if (length < 30) {
								code = code + "^" + objectCode.get(endAddress);
								i++;
							}

						}
						if (checkEnd(i)) {
							length = length + pass.hexaToDecimal(pass.decimalToHexa(pass.hexaToDecimal(pass.LengthOfEnd)
									- pass.hexaToDecimal(pass.getaddresses().get(i - 1))));
						}
						objectProgram.add(
								"T" + "^" + getZeros(startAddress) + "^" + getZero(pass.decimalToHexa(length)) + code);
						i--;
						code = new String();
						length = 0;
					}

				}
				i++;
			}
		}
		for (int index = 0; index < objectProgram.size(); index++) {
			System.out.println(objectProgram.get(index));
		}

	}

	private boolean checkEnd(int i) {
		if (i == pass.getprogram().size() - 1
				&& pass.getprogram().get(i).getOperationCode().trim().toLowerCase().equals("end")) {
			return true;
		}
		return false;
	}

	private String getZero(String decimalToHexa) {
		while (decimalToHexa.length() < 2) {
			decimalToHexa = "0" + decimalToHexa;
		}
		return decimalToHexa;

	}

	private String getZeross(String decimalToHexa) {
		while (decimalToHexa.length() < 4) {
			decimalToHexa = "0" + decimalToHexa;
		}
		return decimalToHexa;

	}

	private boolean valid(String startAddress, int i) {
		if (i < pass.getaddresses().size() - 1) {
			if (pass.hexaToDecimal(pass.getaddresses().get(i + 1)) - pass.hexaToDecimal(startAddress) > 30) {
				return false;
			}
		}
		return true;
	}

	private void getOpcode() {
		String objectcode = new String();
		int i = 0;
		ArrayList<SICline> program = pass.getprogram();
		while (i < pass.getaddresses().size()) {
			if (program.get(i).getOperationCode().trim().toLowerCase().equals("word")) {
				objectcode = handleWordOperand(program.get(i).getOperand().trim());

			} else if (program.get(i).getOperationCode().trim().toLowerCase().equals("byte")) {
				objectcode = handleByteOperand(program.get(i).getOperand().trim());
			} else if (program.get(i).getOperationCode().trim().toLowerCase().equals("rsub")) {
				objectcode = "4C0000";
			} else if (program.get(i).getOperationCode().trim().toLowerCase().equals("resb")) {
				objectcode = null;
			} else if (program.get(i).getOperationCode().trim().toLowerCase().equals("resw")) {
				objectcode = null;
			} else {
				String operand = new String();
				if (program.get(i).getOperand().trim().contains(",")) {
					if (program.get(i).getOperand().trim().toLowerCase().startsWith("0x")) {
						operand = getZeross(program.get(i).getOperand().trim().substring(2,
								program.get(i).getOperand().trim().indexOf(",")));

					} else {
						operand = pass.getSymbolTable().get(program.get(i).getOperand().trim()
								.substring(0, program.get(i).getOperand().trim().indexOf(",")).toLowerCase());
					}

					operand = modify(operand);

				} else {
					if (program.get(i).getOperand().trim().toLowerCase().startsWith("0x")) {
						operand = getZeross(program.get(i).getOperand().trim().substring(2));
					} else {
						operand = pass.getSymbolTable().get(program.get(i).getOperand().trim().toLowerCase());
						if (operand != null) {
							operand = getZeross(operand);
						}
					}
				}
				objectcode = pass.getoperationCode().get(program.get(i).getOperationCode().trim().toLowerCase())
						+ operand;
			}
			objectCode.put(pass.getaddresses().get(i), objectcode);
			i++;
		}
	}

	private String handleWordOperand(String trim) {
		try {
			Integer.parseInt(trim);
			if (trim.startsWith("-")) {
				int f = Integer.parseInt(trim.substring(1));
				return pass.decimalToHexa((binaryToInteger(invertDigits(getzeros(Long.toBinaryString(f)))) + 1));
			} else {
				return getZeros(pass.decimalToHexa(Integer.valueOf(trim)));
			}
		} catch (NumberFormatException e) {
			String[] j = trim.split("x");
			return getZeros(j[1].trim());
		}
	}

	private String getzeros(String decimalToHexa) {
		while (decimalToHexa.length() < 24) {
			decimalToHexa = "0" + decimalToHexa;
		}
		return decimalToHexa;

	}

	public static int binaryToInteger(String binary) {
		char[] numbers = binary.toCharArray();
		int result = 0;
		for (int i = numbers.length - 1; i >= 0; i--)
			if (numbers[i] == '1')
				result += Math.pow(2, (numbers.length - i - 1));
		return result;
	}

	public static String invertDigits(String binaryInt) {
		String result = binaryInt;
		result = result.replace("0", " "); // temp replace 0s
		result = result.replace("1", "0"); // replace 1s with 0s
		result = result.replace(" ", "1"); // put the 1s back in
		return result;
	}

	private String modify(String operand) {

		return pass.decimalToHexa(pass.hexaToDecimal(operand) + 32768);
	}

	private String getZeros(String string) {
		while (string.length() < 6) {
			string = "0" + string;
		}
		return string;
	}

	private String getSpaces(int i) {
		String spaces = "";
		for (int j = 0; j < i; j++) {
			spaces = spaces + " ";
		}
		return spaces;
	}

	private String handleByteOperand(String operand) {
		String code = new String();
		String g = operand.replaceAll(" ", "");
		if (g.toLowerCase().charAt(0) == 'x') {
			operand = g.substring(2, g.length() - 1);
		} else if (g.toLowerCase().charAt(0) == 'c') {
			operand = operand.substring(operand.indexOf("\'") + 1, operand.length() - 1);
			int i = 0;
			while (i < operand.length()) {
				code = code + AsciiTable.get(String.valueOf(operand.charAt(i)));
				i++;
			}
			operand = code;
			code = new String();
		}
		return operand;
	}

	private ArrayList<String> getOPerand(ArrayList<String> intermediate) {
		ArrayList<String> ope = new ArrayList<>();
		int startIndex = 0;
		for (int i = 0; i < intermediate.get(0).length(); i++) {
			if (intermediate.get(0).charAt(i) == '1') {
				startIndex = i;
				break;
			}
		}
		startIndex = intermediate.get(0).length() + 6;
		for (int i = 0; i < intermediate.size(); i++) {
			String line = new String();
			if (startIndex > intermediate.get(i).length()) {
				int sub = startIndex - intermediate.get(i).length();
				line = intermediate.get(i).substring(0, intermediate.get(i).length());
				for (int g = 0; g < sub; g++) {
					line = line + " ";
				}

			} else {
				line = intermediate.get(i).substring(0, startIndex);
			}
			progrmLines.add(line);
		}
		for (int j = 0; j < intermediate.size(); j++) {
			String operand = new String();
			for (int g = startIndex; g < intermediate.get(j).length(); g++) {
				if (intermediate.get(j).charAt(g) == ' ' && !(operand.length() == 0)) {
					break;
				} else {
					operand = operand + String.valueOf(intermediate.get(j).charAt(g));
				}
			}
			ope.add(operand);
		}
		return ope;
	}

	public void getListingFile() {
		Writer writer = null;
		String[] operations = { "add", "and", "clear", "comp", "div", "hio", "j", "jeq", "jgt", "jlt", "jsub", "lda",
				"ldb", "ldch", "ldl", "ldx", "lps", "mul", "norm", "or", "rd", "rmo", "rsub", "shiftl", "shiftr", "sio",
				"ssk", "sta", "stb", "stch", "sti", "stl", "stsw", "stx", "sub", "svc", "td", "tio", "tix", "wd" };
		String locationCode = pass.getaddresses().get(0);
		ArrayList<String> intermediate = pass.getintermediateFile();
		ArrayList<String> operand = getOPerand(intermediate);
		LinkedHashMap<String, String> OPTABLE = pass.getoperationCode();
		LinkedHashMap<String, String> SYMTAB = pass.getSymbolTable();
		int line = 1;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt"), "utf-8"));
			writer.write("line  location   labels   opcode  operands   object Code" + "\n");
			for (int i = 0; i < intermediate.size(); i++) {
				if (i < pass.getaddresses().size()) {
					locationCode = pass.getaddresses().get(i);
				}
				String objectcode = objectCode.get(locationCode);
				if (i == 0) {
					writer.write(line + "     " + locationCode + "       " + progrmLines.get(i).toUpperCase()
							+ "         " + "\n");
					line++;
				}
				if (i == intermediate.size() - 1) {
					writer.write(
							line + "      " + "  " + "       " + progrmLines.get(i).toUpperCase() + "         " + "\n");
				} else if (i > 0) {
					if (line < 10) {
						writer.write(line + "     " + locationCode + "       ");
					} else if (line < 100) {
						writer.write(line + "    " + locationCode + "       ");
					}
					if (objectcode == null) {
						writer.write(progrmLines.get(i).toUpperCase() + "\n");
					} else {
						writer.write(progrmLines.get(i).toUpperCase() + "    " + objectcode + "\n");
					}
					line++;
				}
			}
			if (pass.hexaToDecimal(locationCode) > 32767) {
				writer.write("------------------------------------------------------" + "\n");
				writer.write("ERROR" + "\n");
				writer.write("ADDRESSES ARE OUT OF MEMORY" + "\n");
			}
			writer.write("*--------------------------------------------------------------------------------*" + "\n");
			writer.write("        ----------------------        " + "\n");
			writer.write("        |  Opcode  | Address |" + "\n");
			for (int j = 0; j < operations.length; j++) {
				writer.write("           " + operations[j].toUpperCase());
				if (operations[j].length() == 1) {
					writer.write("           " + OPTABLE.get(operations[j]) + "\n");
				} else if (operations[j].length() == 2) {
					writer.write("          " + OPTABLE.get(operations[j]) + "\n");
				} else if (operations[j].length() == 4) {
					writer.write("        " + OPTABLE.get(operations[j]) + "\n");
				} else if (operations[j].length() == 5) {
					writer.write("       " + OPTABLE.get(operations[j]) + "\n");
				} else if (operations[j].length() == 6) {
					writer.write("      " + OPTABLE.get(operations[j]) + "\n");
				} else {
					writer.write("         " + OPTABLE.get(operations[j]) + "\n");
				}
			}
			writer.write("*--------------------------------------------------------------------------------*" + "\n");
			writer.write("        ---------------------        " + "\n");
			writer.write("        |  Label  | Address |" + "\n");
			writer.write("        ---------------------        " + "\n");
			for (int j = 0; j < SYMTAB.size(); j++) {
				if (SYMTAB.keySet().toArray()[j].toString().length() == 1) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "         "
							+ SYMTAB.values().toArray()[j] + "\n");
				} else if (SYMTAB.keySet().toArray()[j].toString().length() == 2) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "        "
							+ SYMTAB.values().toArray()[j] + "\n");
				} else if (SYMTAB.keySet().toArray()[j].toString().length() == 3) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "       "
							+ SYMTAB.values().toArray()[j] + "\n");
				} else if (SYMTAB.keySet().toArray()[j].toString().length() == 4) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "      "
							+ SYMTAB.values().toArray()[j] + "\n");
				} else if (SYMTAB.keySet().toArray()[j].toString().length() == 5) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "     "
							+ SYMTAB.values().toArray()[j] + "\n");
				} else if (SYMTAB.keySet().toArray()[j].toString().length() == 6) {
					writer.write("           " + SYMTAB.keySet().toArray()[j].toString().toUpperCase() + "    "
							+ SYMTAB.values().toArray()[j] + "\n");
				}
				writer.write("        ---------------------        " + "\n");

			}
			writer.write("*--------------------------------------------------------------------------------*" + "\n");
			writer.write("Object Program " + "\n");
			for (int i = 0; i < objectProgram.size(); i++) {
				writer.write(objectProgram.get(i) + "\n");
			}

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
		}
	}
}
