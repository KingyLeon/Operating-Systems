package taskA;

import java.io.*;
import java.util.*;

public class TaskA {

	public static void main(String[] args) throws IOException {

		System.out.println("Operating Systems Coursework");
		System.out.println("Name: Leon King"); // display your name in here
		System.out.println("Please enter your commands - cat, cut, sort, uniq, wc or |");

		Scanner scanner = new Scanner(System.in);
		System.out.print(">> ");
		String input = scanner.nextLine().trim();
		execute(input);
		scanner.close();
	}

	// read the command from the terminal
	public static void execute(String input) throws IOException {
		String[] commands = input.split("\\|");
		BufferedReader reader = null;
		BufferedWriter writer = null;

		// Break command into functional tokens
		for (int i = 0; i < commands.length; i++) {
			String[] tokens = commands[i].trim().split(" ");
			Process process;
			
			if (i == 0) {
				if (tokens[0] == "cat") {
					reader = new BufferedReader(new FileReader(tokens[1]));
					System.out.println(tokens.length + tokens[1] + reader.readLine());
				} else if (tokens[i] == "cut -f") {
					// leave to last
				} else if (tokens[i] == "sort") {
					/*	List<String> lines = new ArrayList<>();
					String line;
					
					while ((line = reader.readLine()) != null) {
						lines.add(line);
					}
					lines.sort(null);
					reader = new BufferedReader(new FileReader(tokens[1]));
					reader.readLine(); */
				} else if (tokens[i] == "uniq") {

				} else if (tokens[i] == "wc") {

				}

			} else {
				ProcessBuilder builder = new ProcessBuilder(commands[i - 1].trim().split("\\s+"));
				process = builder.start();
				reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				System.out.println("Fail");
			}
			if (i == commands.length - 1) {

			} else if (tokens[0].equals("cut")) {
			} else if (tokens[0].equals("sort")) {
			} else if (tokens[0].equals("uniq")) {
			} else if (tokens[0].equals("wc")) {
			}

		}
	}

	// read the text file
	// Thread thread1 = new Thread(new Runnable());
}
