package taskA;

import java.io.*;
import java.nio.channels.Pipe;
import java.util.*;

/**
 * 
 */
public class TaskA {

	public static void main(String[] args) throws IOException {

		System.out.println("Operating Systems Coursework");
		System.out.println("Name: Leon King"); // display your name in here
		System.out.println("Please enter your commands - cat, cut, sort, uniq, wc or |");

		console();
	}

	public static void console() {
		Scanner scanner = new Scanner(System.in);
		String input = "";
		try {
			while (!input.equals("close")) {
				System.out.print(">> ");
				input = scanner.nextLine().trim();
				execute(input);
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// read the command from the terminal
	public static void execute(String input) throws IOException {
		String[] commands = input.split("\\|");
		List<String> prevInput = null;

		// Break command into functional tokens
		for (int i = 0; i < commands.length; i++) {
			String[] tokens = commands[i].trim().split(" ");
			if (i == 0) {
				List<String> lines = new ArrayList<>();

				// Cat function
				if (tokens[0].equals("cat")) {
					lines = lineSplitter(tokens[1]);
					prevInput = cat(lines);

					// Cut Function
				} else if (tokens[0].equals("cut")) {
					String delim = ",";
					lines = lineSplitter(tokens[tokens.length - 1]);

					if (tokens[3].equals("-d")) {
						delim = tokens[4].substring(1, tokens[4].length() - 1);
					}
					prevInput = cut(lines, tokens[2], delim);

					// Sort Function
				} else if (tokens[0].equals("sort")) {
					lines = lineSplitter(tokens[1]);
					prevInput = sort(lines);

					// Uniq Function
				} else if (tokens[0].equals("uniq")) {
					lines = lineSplitter(tokens[1]);
					prevInput = uniq(lines);

					// wc Function
				} else if (tokens[0].equals("wc")) {
					if (!tokens[1].equals("-l")) {
						lines = lineSplitter(tokens[1]);
						prevInput = wc(lines, "null");
					} else {
						lines = lineSplitter(tokens[2]);
						prevInput = wc(lines, tokens[1]);
					}
				}
			} else {
				if (tokens[0].equals("cut")) {
					String delim = ",";
					if (tokens.length < 3) {
						if (tokens[3].equals("-d")) {
							delim = tokens[i + 4].substring(1, tokens[i + 4].length() - 1);
						}
					}
					prevInput = cut(prevInput, tokens[2], delim);
					// Sort Function
				} else if (tokens[0].equals("sort")) {
					prevInput = sort(prevInput);
				} else if (tokens[0].equals("uniq")) {
					prevInput = uniq(prevInput);
				} else if (tokens[0].equals("wc")) {
					if (tokens.length > 1) {
						if (tokens[1].equals("-l")) {
							prevInput = wc(prevInput, tokens[1]);
						}
					} else {
						prevInput = wc(prevInput, "null");
					}
				}
			}
		}
		for (String x : prevInput) {
			System.out.println(x);
		}
	}

	// Return list containing each line of text file
	public static List<String> cat(List<String> lines) throws FileNotFoundException {
		List<String> output = new ArrayList<>();

		for (String x : lines) {
			// System.out.println(x);
			output.add(x);
		}
		return output;
	}

	// Return a list of fields from specified columns
	public static List<String> cut(List<String> lines, String fieldN, String delim) {
		String[] splitField = null;
		String state = "normal";
		int num1 = 0;
		int num2 = 0;
		if (fieldN.contains("-")) {
			splitField = fieldN.split("-");
			 num1 = (Integer.parseInt(splitField[0]) - 1);
			 num2 = (Integer.parseInt(splitField[1]) - 1);
			state = "range";
		}
		if (fieldN.contains(",")) {
			splitField = fieldN.split(",");
			 num1 = (Integer.parseInt(splitField[0]) - 1);
			 num2 = (Integer.parseInt(splitField[1]) - 1);
			state = "select";
		}
		List<String> output = new ArrayList<>();

		// returns an interval of columns
		if (state.equals("range")) {
			for (String x : lines) {
				String[] fields;
				fields = x.split(delim);
				
				String concat = "";
				for (int i = num1; i <= num2; i++) {
					concat +=fields[i] + ",";
				}
				output.add(concat.substring(0, concat.length() -1));
			}
			//returns selected columns
		} else if (state.equals("select")) {
			for (String x : lines) {
				String[] fields;
				fields = x.split(delim);

				output.add(fields[num1] + "," + fields[num2]);
			}
		} else {
			int field = Integer.parseInt(fieldN);
			for (String x : lines) {
				String[] fields;
				fields = x.split(delim);
				output.add(fields[field - 1]);
			}
		}
		return output;
	}

	// Return a list of lines sorted alphanumerically
	public static List<String> sort(List<String> lines) {
		Collections.sort(lines);
		List<String> output = new ArrayList<>();
		for (String x : lines) {
			// System.out.println(x);
			output.add(x);
		}
		return output;

	}
	// Return a list of unique lines
	public static List<String> uniq(List<String> lines) {
		List<String> output = new ArrayList<>();
		for (String x : lines) {
			if (!output.contains(x)) {
				output.add(x);
			}
		}
		return output;
	}
	// Return a count of lines, or words characters and lines
	public static List<String> wc(List<String> lines, String param) {
		List<String> output = new ArrayList<>();
		int lineNum = 0;
		int charNum = 0;
		int wordNum = 0;
		String lineOut;
		String charOut;
		String wordOut;
		lineNum = lines.size();

		// return only line size
		if (param.equals("-l")) {
			output.add("" + lineNum);

		} else {
			for (String x : lines) {
				String[] words = x.split("\\s+");
				wordNum += words.length;
				charNum += x.length();
			}
			lineOut = "" + lineNum;
			charOut = "" + charNum;
			wordOut = "" + wordNum;
			output.add(lineOut + " " + charOut + " " + wordOut);
		}
		return output;
	}

	// Function to help split a file into a list of strings
	public static List<String> lineSplitter(String file) {
		BufferedReader reader;
		List<String> lines = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
}
