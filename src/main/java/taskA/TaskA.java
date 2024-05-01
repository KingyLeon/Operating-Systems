package taskA;

import java.io.*;
import java.nio.channels.Pipe;
import java.util.*;

/**
 * 
 */
/**
 * 
 */
public class TaskA {

	public static void main(String[] args) throws IOException {

		System.out.println("Operating Systems Coursework");
		System.out.println("Name: Leon King"); // display your name in here
		System.out.println("Please enter your commands - cat, cut, sort, uniq, wc or |");
		
		// Create a writer thread
        Thread writerThread = new Thread(() -> {
            try {
                String message = "Hello, Pipe!";
                pos.write(message.getBytes());
                pos.close(); // Close the output stream to signal end of data
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Create a reader thread
        Thread readerThread = new Thread(() -> {
            try {
                int data;
                while ((data = pis.read()) != -1) {
                    System.out.print((char) data);
                }
                pis.close(); // Close the input stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

		console();
	}

	public static void console() {
		Scanner scanner = new Scanner(System.in);
		String input = null;
		while (input != "close") {
			System.out.print(">> ");
			input = scanner.nextLine().trim();
			try {
				execute(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		scanner.close();
	}

	// read the command from the terminal
	public static void execute(String input) throws IOException {
		String[] commands = input.split("\\|");

		// Break command into functional tokens
		for (int i = 0; i < commands.length; i++) {
			String[] tokens = commands[i].trim().split(" ");
			Process process;

			if (i == 0) {
				System.out.println(tokens[i]);
				if (tokens[i].equals("cat")) {
					cat(tokens[i + 1]);
				} else if (tokens[i].equals("cut")) {
					cut(tokens[i + 5], tokens[i + 2], tokens[i + 4].substring(1, tokens[i + 4].length() - 1));
				} else if (tokens[i].equals("sort")) {
					System.out.println("SORT ACCESSED");
					sort(tokens[i + 1]);
				} else if (tokens[i].equals("uniq")) {
					uniq(tokens[i + 1]);
				} else if (tokens[i].equals("wc")) {
					if (!tokens[i + 1].equals("-l")) {
						wc(tokens[i + 1], "null");
					} else {
						wc(tokens[i + 2], tokens[i + 1]);
					}
				} else if (tokens[i].equals("|")) {
					Pipe.SinkChannel pipe1;
				}
			}
		}
	}

	// read the text file
	public static void cat(String file) throws FileNotFoundException {
		List<String> lines = new ArrayList<>();
		lines = lineSplitter(file);
		for (String x : lines) {
			System.out.println(x);
		}
	}

	public static void cut(String file, String fieldN, String delim) {
		int field = Integer.parseInt(fieldN);
		List<String> lines = new ArrayList<>();
		List<String> output = new ArrayList<>();
		lines = lineSplitter(file);

		for (String x : lines) {
			String[] fields;
			fields = x.split(delim);
			output.add(fields[field]);
		}
		for (String x : output) {
			System.out.println(x);
		}
	}

	public static void sort(String file) {
		List<String> lines = new ArrayList<>();
		lines = lineSplitter(file);
		Collections.sort(lines);
		for (String x : lines) {
			System.out.println(x);
		}

	}

	public static void uniq(String file) {
		BufferedReader reader;
		List<String> lines = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!lines.contains(line)) {
					lines.add(line);
				}
			}
			reader.close();
			for (String x : lines) {
				System.out.println(x);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void wc(String file, String param) {
		List<String> lines = new ArrayList<>();
		lines = lineSplitter(file);
		int lineNum = 0;
		int charNum = 0;
		int wordNum = 0;
		lineNum = lines.size();

		if (param.equals("-l")) {
			// return only line size
			System.out.println(lineNum + " " + file);
		} else {
			for (String x : lines) {
				wordNum += x.split("\s+").length;
				for (char y : x.toCharArray()) {
					charNum++;
				}
			}
			System.out.println(lineNum + " " + wordNum + " " + charNum + " " + file);
		}

	}

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
