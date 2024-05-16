package taskC;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskC {

	public static void main(String[] args) {
		// Your code here.
		List<String> virtualAddresses = readTable("TaskC.txt");
		List<String[]> TLBEntries = Retrieve_TLB_Entries("TaskC.txt");
		List<String[]> pageEntries = Retrieve_PageTable_Entries("TaskC.txt");

		// Clear output file
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(Paths.get("taskc-sampleoutput.txt"));
			writer.write("");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Process ran for each address
		for (String address : virtualAddresses) {
			String pageNum = address.substring(2, 3);
			Boolean checkTLB = checkTLB(TLBEntries, pageNum);
			Boolean checkPageFault = checkForPageFault(pageEntries, TLBEntries, pageNum);
			String result = getResult(checkTLB, checkPageFault);
			TLBEntries = changeTLB(result, pageNum, TLBEntries, pageEntries);
			buildOutput(address, pageNum, result, TLBEntries, pageEntries, "taskc-sampleoutput.txt");

			System.out.println("TLB After the memory access " + address);
			System.out.println("Result: " + result);

			for (String[] x : TLBEntries) {
				System.out.println(x[0] + ", " + x[1] + ", " + x[2] + "," + x[3]);
			}
		}
	}
	
	// Changes value of all LRU, returns altered TLB
	public static List<String[]> changeLRU(List<String[]> TLB, String pageNum) {
		for (String[] row : TLB) {
			if (row[1].equals(pageNum)) {
				int TLBVal = Integer.parseInt(row[3]);
				row[3] = "5";

				for (String[] row2 : TLB) {
					if (Integer.parseInt(row2[3]) > TLBVal) {
						row2[3] = "" + (Integer.parseInt(row2[3]) - 1);
					}
				}
				break;
			}
		}
		return TLB;
	}
	// Alters row in TLB, returns the altered TLB table
	public static List<String[]> changeTLB(String result, String pageNum, List<String[]> TLB,
			List<String[]> pageEntries) {
		if (result.equals("Hit")) {
			TLB = changeLRU(TLB, pageNum);
		} else if (result.equals("Miss") || result.equals("Page Fault")) {
			String[] LRU_row = null;
			for (String[] row : TLB) {
				if (row[3].equals("1")) {
					LRU_row = row;
				}
			}

			String[] replacementRow = searchPageTable(pageEntries, pageNum);
			for (String[] row : TLB) {
				if (Arrays.equals(LRU_row, row)) {
					row[0] = replacementRow[1];
					row[1] = replacementRow[0];
					row[2] = replacementRow[2];
					TLB = changeLRU(TLB, replacementRow[0]);
				}
			}
		}
		return TLB;
	}
	// Searches pageTable for appropriate row, returns fields of row
	public static String[] searchPageTable(List<String[]> pageEntries, String pageNum) {
		String[] Row = null;
		for (String[] row : pageEntries) {
			if (row[0].equals(pageNum)) {
				Row = row;
			}
		}
		return Row;
	}

	// Build the output for the file
	public static void buildOutput(String address, String pageNum, String result, List<String[]> TLB,
			List<String[]> pageEntries, String file) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.append("# After the memory access " + address + "\n");
			writer.append("#Address, Result (Hit, Miss, PageFault)" + "\n");
			writer.append(address + "," + result + "\n");
			writer.append("#updated TLB" + "\n");
			writer.append("#Valid, Tag, Physical Page #, LRU" + "\n");
			for (String[] row : TLB) {
				writer.append(row[0] + "," + row[1] + "," + row[2] + "," + row[3] + "\n");
			}
			writer.append("#updated Page table" + "\n");
			writer.append("#Index, Valid, Physical Page or on Disk" + "\n");
			for (String[] row : pageEntries) {
				writer.append(row[0] + "," + row[1] + "," + row[2] + "\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Returns result hit
	public static String getResult(Boolean checkTLB, Boolean checkPageFault) {
		String Result = "Miss";
		if (checkTLB) {
			Result = "Hit";
		}
		if (checkPageFault) {
			Result = "Page Fault";
		}
		return Result;
	}

	// Checks for pageFault
	public static boolean checkForPageFault(List<String[]> PageEntries, List<String[]> TLB, String pageNumber) {
		Boolean outcome = false;
		if (checkTLB(TLB, pageNumber) == true) {
			for (String[] row : TLB) {
				if (row[1].equals(pageNumber) && row[3].equals("Disk")) {
					outcome = true;
				}
			}
		} else {
			for (String[] row : PageEntries) {
				if (row[0].equals(pageNumber) && row[2].equals("Disk")) {
					outcome = true;
				}
			}
		}
		return outcome;
	}

	// Checks if Tag present in TLB
	public static boolean checkTLB(List<String[]> TLB, String pageNumber) {
		Boolean outcome = false;

		for (String[] row : TLB) {
			if (row[1].equals(pageNumber)) {
				outcome = true;
			}
		}
		return outcome;
	}

	// Retrieve the Page Table
	public static List<String[]> Retrieve_PageTable_Entries(String file) {
		BufferedReader reader;
		List<String[]> entries = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			int lineNumber = 0;
			/*
			 * while ((line = reader.readLine()) != null) { if (lineNumber > 15 &&
			 * lineNumber < 28) { entries.add(line.trim().split(",")); } lineNumber++; }
			 */
			boolean pageTable = false;
			while ((line = reader.readLine()) != null) {
				if (pageTable) {
					entries.add(line.trim().split(","));
				} else if (line.trim().equals(("#Index,Valid,Physical Page or On Disk").trim())) {
					pageTable = true;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	// Retrieve TLB entries
	public static List<String[]> Retrieve_TLB_Entries(String file) {
		BufferedReader reader;
		List<String[]> entries = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			Boolean pageTable = false;
			while ((line = reader.readLine()) != null) {
				if (line.equals("#Initial Page table")) {
					break;
				}
				if (pageTable) {
					entries.add(line.split(","));
				} else if (line.trim().equals(("#Valid, Tag, Physical Page #, LRU").trim())) {
					pageTable = true;
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return entries;
	}

	// Return a list of the pageNumbers
	public static List<String> pageNumbers(List<String> addresses) {
		List<String> pageNumbers = new ArrayList<>();
		for (String address : addresses) {
			pageNumbers.add(address.substring(2, 4));
		}
		return pageNumbers;
	}

	// Method to Read the Virtual Addresses, return them as a list
	public static List<String> readTable(String file) {
		BufferedReader reader;
		List<String> lines = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.equals("#Initial TLB")) {
					break;
				} else if (!line.equals("#Address")) {
					lines.add(line);
				}
			}
			reader.close();
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
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
