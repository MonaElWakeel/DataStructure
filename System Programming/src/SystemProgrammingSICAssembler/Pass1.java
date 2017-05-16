package SystemProgrammingSICAssembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Pass1 {
	public static LinkedHashMap<String, String> SYMTAB = new LinkedHashMap<>();
	private static ArrayList<String> label = new ArrayList<>();
	private static ArrayList<String> operand = new ArrayList<>();
	private int LOCCTR;
	public String LengthOfEnd = new String();
	public static int Max_Size = 8388608;
	public boolean end = false;
	public static int begin = 0;
	public String startAdress = new String();
	private static LinkedHashMap<String, String> errorMessage = new LinkedHashMap<>();

	public static LinkedHashMap<String, String> getErrorMessage() {
		return errorMessage;
	}

	public static void setErrorMessage(String key, String value) {
		errorMessage.put(key, value);
	}

	public static ArrayList<SICline> program = new ArrayList<>();
	public ArrayList<String> addresses = new ArrayList<>();
	public static ArrayList<String> intermediateFile = new ArrayList<>();
	public static final LinkedHashMap<String, String> OPTABLE = makeMap();

	private static LinkedHashMap<String, String> makeMap() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("add", "18");
		result.put("and", "40");
		result.put("clear", "B4");
		result.put("comp", "28");
		result.put("div", "24");
		result.put("hio", "F4");
		result.put("j", "3C");
		result.put("jeq", "30");
		result.put("jgt", "34");
		result.put("jlt", "38");
		result.put("jsub", "48");
		result.put("lda", "00");
		result.put("ldb", "68");
		result.put("ldch", "50");
		result.put("ldl", "08");
		result.put("ldx", "04");
		result.put("lps", "D0");
		result.put("mul", "20");
		result.put("norm", "C8");
		result.put("or", "44");
		result.put("rd", "D8");
		result.put("rmo", "AC");
		result.put("rsub", "4C");
		result.put("shiftl", "A4");
		result.put("shiftr", "A8");
		result.put("sio", "F0");
		result.put("ssk", "EC");
		result.put("sta", "0C");
		result.put("stb", "78");
		result.put("stch", "54");
		result.put("sti", "D4");
		result.put("stl", "14");
		result.put("stsw", "E8");
		result.put("stx", "10");
		result.put("sub", "1C");
		result.put("svc", "B0");
		result.put("td", "E0");
		result.put("tio", "F8");
		result.put("tix", "2C");
		result.put("wd", "DC");
		return result;
	}

	public static void main(String[] args) {
		Pass1 n = new Pass1();
		n.load(new File("prog1"));
		n.getImmediateFile();
		emptyFile();
		if (errorMessage.isEmpty()) {
			Pass2 m = new Pass2(n);
			m.load();
			m.getListingFile();
		}
	}

	private static void emptyFile() {
		File file = new File("output.txt");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.print("");
		writer.close();

	}

	public void load(File file) {
		end = false;
		CheckFormat f = new CheckFormat();
		boolean label = false;
		try {
			BufferedReader words = new BufferedReader(new FileReader(file));
			SICline LINE = new SICline();
			boolean start = true;
			for (String line; (line = words.readLine()) != null;) {
				if (line.trim().length() > 0 && line.trim().charAt(0) != '.') {
					line = line.replaceAll("\t", "    ");
					LINE = getLine(line);
					f.checkLineFormat(LINE);
					if (start) {
						if (LINE.getOperationCode().toLowerCase().trim().equals("start")) {
							if (NOTHEX(LINE.getOperand().trim().toLowerCase())
									|| hexaToDecimal(LINE.getOperand().trim().toLowerCase()) > 32767) {
								setErrorMessage(String.valueOf(begin),
										"operand of start is wrong check if hex or check if out of memory");
							} else {
								addresses.add(LINE.getOperand().trim().toLowerCase());
								LOCCTR = hexaToDecimal(LINE.getOperand().trim().toLowerCase());
								startAdress = LINE.getOperand().trim().toLowerCase();
							}
						} else {
							addresses.add("0000");
							LOCCTR = 0;
							startAdress = "0000";
							SICline defaultLine = new SICline();
							defaultLine.setLabel("FILE0   ");
							defaultLine.setBlank1(" ");
							defaultLine.setBlank2(" ");
							defaultLine.setOperationCode("start  ");
							defaultLine.setOperand("0000");
							program.add(defaultLine);
							String INTERline = getINTRline(defaultLine);
							intermediateFile.add(INTERline);
							setErrorMessage(String.valueOf(begin), " ");
							begin++;
						}
						if (LINE.getLabel() != null && !LINE.getLabel().trim().equals("") && begin == 0) {
							SYMTAB.put(LINE.getLabel().trim().toLowerCase(), decimalToHexa(LOCCTR));
						}
					}
					if (!LINE.getOperationCode().toLowerCase().trim().equals("end") && begin != 0) {
						if (LINE.getLabel() != null && !LINE.getLabel().trim().equals("")) {
							if (SYMTAB.containsKey(LINE.getLabel().toLowerCase().trim())) {
								System.out.println("error symbol already exists");
							} else {
								SYMTAB.put(LINE.getLabel().trim().toLowerCase(), decimalToHexa(LOCCTR));
							}
						}
						if (LINE.getOperationCode() != null && !LINE.getOperationCode().trim().equals("")) {
							if (LINE.getOperationCode().toLowerCase().trim().equals("word")) {
								if (LINE.getOperand().contains("x")) {
									String[] j = LINE.getOperand().split("x");
									LINE.setOperand(getOX(j, "x"));
								} else if (LINE.getOperand().contains("X")) {
									String[] j = LINE.getOperand().split("X");
									LINE.setOperand(getOX(j, "X"));
								}
								addresses.add(getNum(decimalToHexa(LOCCTR)));
								LOCCTR = LOCCTR + 3;
							} else if (LINE.getOperationCode().toLowerCase().trim().equals("resw")) {
								boolean ox = false;
								String g = new String();
								if (LINE.getOperand().contains("x")) {
									ox = true;
									String[] j = LINE.getOperand().split("x");
									LINE.setOperand(getOX(j, "x"));
								} else if (LINE.getOperand().contains("X")) {
									ox = true;
									String[] j = LINE.getOperand().split("X");
									LINE.setOperand(getOX(j, "X"));
								}
								if (ox) {
									g = String.valueOf(
											hexaToDecimal(LINE.getOperand().trim().toLowerCase().substring(2)));
								} else {
									g = LINE.getOperand().trim().toLowerCase();
								}
								addresses.add(getNum(decimalToHexa(LOCCTR)));
								LOCCTR = LOCCTR + 3 * Integer.parseInt(g);
								ox = false;
								g = new String();

							} else if (LINE.getOperationCode().toLowerCase().trim().equals("resb")) {
								boolean ox = false;
								String g = new String();
								if (LINE.getOperand().contains("x")) {
									ox = true;
									String[] j = LINE.getOperand().split("x");
									LINE.setOperand(getOX(j, "x"));
								} else if (LINE.getOperand().contains("X")) {
									ox = true;
									String[] j = LINE.getOperand().split("X");
									LINE.setOperand(getOX(j, "X"));
								}
								if (ox) {
									g = String.valueOf(
											hexaToDecimal(LINE.getOperand().trim().toLowerCase().substring(2)));
								} else {
									g = LINE.getOperand().trim().toLowerCase();
								}
								addresses.add(getNum(decimalToHexa(LOCCTR)));
								LOCCTR = LOCCTR + Integer.parseInt(LINE.getOperand().trim().toLowerCase());
								ox = false;
								g = new String();
							} else if (LINE.getOperationCode().toLowerCase().trim().equals("byte")) {
								int length = 0;
								if (LINE.getOperand().toLowerCase().trim().charAt(0) == 'x') {
									LINE.setOperand(getX(LINE.getOperand()));
									length = getLength(LINE.getOperand());
								} else {
									LINE.setOperand(getC(LINE.getOperand()));
									length = findLength(LINE.getOperand().trim().toLowerCase());
								}
								addresses.add(getNum(decimalToHexa(LOCCTR)));
								LOCCTR = LOCCTR + Integer.parseInt(String.valueOf(length));

							} else if (!OPTABLE.containsKey(LINE.getOperationCode().toLowerCase().trim())) {
								setErrorMessage(String.valueOf(begin), "error operation doesn't exist");
							} else {
								if ((LINE.getOperand() != null && LINE.getOperand().trim().length() != 0)
										&& LINE.getOperand().contains(",")) {
									LINE.setOperand(getOperand(LINE.getOperand()));
								} else if ((LINE.getOperand() != null && LINE.getOperand().trim().length() != 0)
										&& checkOX(LINE.getOperand().trim())) {
									if (LINE.getOperand().contains("x")) {
										String[] j = LINE.getOperand().split("x");
										LINE.setOperand(getOX(j, "x"));
									} else if (LINE.getOperand().contains("X")) {
										String[] j = LINE.getOperand().split("X");
										LINE.setOperand(getOX(j, "X"));
									}
								}
								addresses.add(getNum(decimalToHexa(LOCCTR)));
								LOCCTR = LOCCTR + 3;

							}
						}

					} else if (LINE.getOperationCode().toLowerCase().trim().equals("end")) {
						if (LINE.getLabel() != null && !LINE.getLabel().trim().isEmpty()
								&& !LINE.getLabel().equals(new String())) {
							end = true;
							label = true;
						} else {
							LengthOfEnd = getNum(decimalToHexa(LOCCTR));
							end = true;
						}
					}
					program.add(LINE);
					String INTERline = getINTRline(LINE);
					intermediateFile.add(INTERline);

					start = false;
					begin++;
				}

			}
			for (int i = 0; i < addresses.size(); i++) {
				System.out.println(addresses.get(i));
			}
			if (!end) {
				intermediateFile.add("THERE IS NO END STATEMENT");
				setErrorMessage(String.valueOf(begin), "ERROR");
			} else if (end && label) {
				intermediateFile.add("THERE IS LABEL IN END STATEMENT");
				setErrorMessage(String.valueOf(begin), "ERROR");
			}
			check();
			label = false;
			if (!errorMessage.isEmpty() && errorMessage.size() == 1 && errorMessage.containsValue(" ")) {
				errorMessage = new LinkedHashMap<>();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	private void check() {

		boolean flag = true;
		for (int i = 0; i < operand.size(); i++) {
			flag = false;
			for (int j = 0; j < Pass1.label.size(); j++) {
				if (operand.get(i).equals(Pass1.label.get(j))) {
					flag = true;
				}
			}
			if (flag == false) {
				intermediateFile.add("ERROR operand  ((" + operand.get(i) + "))   is not defined");
				setErrorMessage(String.valueOf(begin), "");

			}
		}

	}

	public boolean checkOX(String split) {
		if (!split.contains("x")) {
			return false;
		}
		String[] j = split.split("x");
		if (j.length != 2) {
			return false;
		}
		if (!j[0].trim().equals("0")) {
			return false;
		}
		if (NOTHEX(j[1].trim())) {
			return false;
		}

		return true;
	}

	private boolean NOTHEX(String trim) {
		return !isHexadecimal(trim);
	}

	public static boolean isHexadecimal(String value) {
		return value.matches("\\p{XDigit}+");
	}

	private String getOX(String[] j, String l) {
		int t = j[0].length();
		int r = j[0].trim().length();
		int c = j[1].length();
		int y = j[1].trim().length();
		int length = t - r + c - y;
		String temp = j[0].trim() + l + j[1].trim();
		for (int i = 0; i < length; i++) {
			temp = temp + " ";
		}
		return temp;
	}

	private String getOperand(String operand) {
		String op = operand.replace(" ", "");
		int space = operand.length() - op.length();
		operand = op;
		for (int i = 0; i < space; i++) {
			operand = operand + " ";
		}
		return operand;
	}

	private String getC(String operand) {
		String op = operand.substring(operand.indexOf('\''));
		String u = operand.trim().charAt(0) + op;
		for (int i = 0; i < (operand.length() - op.length() - 1); i++) {
			u = u + " ";
		}
		operand = u;
		return operand;
	}

	private int getLength(String operand) {
		operand = operand.trim().substring(2, operand.trim().length() - 1);
		if (operand.trim().length() % 2 == 0) {
			return operand.length() / 2;
		} else {
			return operand.trim().length() / 2 + 1;
		}

	}

	private String getX(String operand) {
		String op = operand.trim().replace(" ", "");
		int space = operand.length() - op.length();
		operand = op;
		for (int i = 0; i < space; i++) {
			operand = operand + " ";
		}
		return operand;
	}

	private String getINTRline(SICline LINE) {
		String line = new String();
		if (LINE.getBlank2() == null) {
			line = LINE.getLabel() + LINE.getBlank1() + LINE.getOperationCode();

		} else if (LINE.getOperand() == null) {
			line = LINE.getLabel() + LINE.getBlank1() + LINE.getOperationCode() + LINE.getBlank2();

		} else if (LINE.getComment() == null) {
			line = LINE.getLabel() + LINE.getBlank1() + LINE.getOperationCode() + LINE.getBlank2() + LINE.getOperand();

		} else {
			line = LINE.getLabel() + LINE.getBlank1() + LINE.getOperationCode() + LINE.getBlank2() + LINE.getOperand()
					+ LINE.getComment();
		}

		return line;
	}

	private int findLength(String trim) {
		trim = trim.trim();
		trim = trim.substring(2, trim.length() - 1);
		return trim.length();
	}

	private SICline getLine(String line) {
		SICline separate = new SICline();
		int index = 0;
		while (index < line.length()) {
			if (index < 8) {
				separate.setLabel(separate.getLabel() + line.charAt(index));
			} else if (index == 8) {
				separate.setBlank1(String.valueOf(line.charAt(index)));
			} else if (index > 8 && index < 15) {
				separate.setOperationCode(separate.getOperationCode() + line.charAt(index));
			} else if (index > 14 && index < 17) {
				separate.setBlank2(separate.getBlank2() + line.charAt(index));
			} else if (index > 16 && index < 35) {
				separate.setOperand(separate.getOperand() + line.charAt(index));
			} else if (index > 34 && index < 66) {
				separate.setComment(separate.getComment() + line.charAt(index));
			}

			index++;
		}
		if (separate.getLabel().equals(new String())) {
			separate.setLabel(null);
		}
		if (separate.getBlank1().equals(new String())) {
			separate.setBlank1(null);
		}
		if (separate.getOperationCode().equals(new String())) {
			separate.setOperationCode(null);
		}
		if (separate.getBlank2().equals(new String())) {
			separate.setBlank2(null);
		}
		if (separate.getOperand().equals(new String())) {
			separate.setOperand(null);
		}
		if (separate.getComment().equals(new String())) {
			separate.setComment(null);
		}
		separate = getWithoutspaces(separate);
		return separate;
	}

	public ArrayList<String> getLabel() {
		return label;
	}

	public void setLabel(String label) {
		Pass1.label.add(label);
	}

	public ArrayList<String> getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		Pass1.operand.add(operand);
	}

	private SICline getWithoutspaces(SICline separate) {
		separate.setLabel(correct(separate.getLabel()));
		separate.setOperationCode(correct(separate.getOperationCode()));
		separate.setOperand(correct(separate.getOperand()));
		separate.setComment(correct(separate.getComment()));
		return separate;
	}

	private String correct(String label) {
		if (label == null || label.trim().equals("")) {
			return label;
		}
		int i = 0;
		while (i < label.length() && label.charAt(0) == ' ') {
			label = label.substring(1) + ' ';

			i++;

		}
		return label;
	}

	public String decimalToHexa(int decimalNumber) {
		String hexadecimalNumber = "";
		String digits = "0123456789ABCDEF";
		int base = 16;
		if (decimalNumber <= 0) {
			return "0";
		}
		while (decimalNumber > 0) {
			int remain = decimalNumber % base;
			hexadecimalNumber = digits.charAt(remain) + hexadecimalNumber;
			decimalNumber = decimalNumber / base;
		}
		return hexadecimalNumber;
	}

	private String getNum(String hexadecimalNumber) {
		while (hexadecimalNumber.length() < 4) {
			hexadecimalNumber = "0" + hexadecimalNumber;
		}
		return hexadecimalNumber;

	}

	public int hexaToDecimal(String hexadecimalNumber) {
		int decimalNumber = 0;
		String hexaNumber = hexadecimalNumber.toUpperCase();
		String digits = "0123456789ABCDEF";
		int digit = 0;
		for (int i = hexaNumber.length() - 1; i >= 0; i--) {
			char hexadigit = hexaNumber.charAt(i);
			int decimaldigit = digits.indexOf(hexadigit);
			decimalNumber = (int) (decimalNumber + (decimaldigit * (Math.pow(16, digit))));

			digit++;
		}
		return decimalNumber;
	}

	public void getImmediateFile() {
		Writer writer = null;
		String[] operations = { "add", "and", "clear", "comp", "div", "hio", "j", "jeq", "jgt", "jlt", "jsub", "lda",
				"ldb", "ldch", "ldl", "ldx", "lps", "mul", "norm", "or", "rd", "rmo", "rsub", "shiftl", "shiftr", "sio",
				"ssk", "sta", "stb", "stch", "sti", "stl", "stsw", "stx", "sub", "svc", "td", "tio", "tix", "wd" };
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("IntermediateFile.txt"), "utf-8"));
			for (int i = 0; i < intermediateFile.size(); i++) {

				if (!getErrorMessage().isEmpty() && getErrorMessage().containsKey(String.valueOf(i))) {
					writer.write(intermediateFile.get(i).toUpperCase() + "           "
							+ getErrorMessage().get(String.valueOf(i)) + "\n");
				} else {
					writer.write(intermediateFile.get(i).toUpperCase() + "\n");
				}
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

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}
	}

	public ArrayList<SICline> getprogram() {

		return Pass1.program;
	}

	public ArrayList<String> getaddresses() {

		return this.addresses;
	}

	public ArrayList<String> getintermediateFile() {
		return intermediateFile;
	}

	public LinkedHashMap<String, String> getoperationCode() {

		return OPTABLE;
	}

	public LinkedHashMap<String, String> getSymbolTable() {

		return SYMTAB;
	}
}
