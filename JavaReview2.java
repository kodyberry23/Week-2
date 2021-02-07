package javaReview2;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This assignment is intended to continue our focus on working with data in
 * Java. In this case, we'll focus on reading and writing text files. We often
 * need to read data from text files and write out data or process information
 * to files as well. In 1410 you likely worked with "file handling," which is
 * the basic set of concepts behind our discussion this week and the premise of
 * this assignment.
 * 
 * @author Kody Berry
 *
 */
public class JavaReview2 {
	// constants
	static final String CSV_PATH = "SLCDecember2020Temperatures.csv";
	static final String REPORT_PATH = "TemperaturesReport.txt";
	static final String LINE = "--------------------------------------------------------------\n";
	static final String SCALE_LABEL = LINE + "      1   5    10   15   20   25   30   35   40   45   50\n"
			+ "      |   |    |    |    |    |    |    |    |    |    |\n" + LINE;
	static final int N_DAYS = 31;

	static int highestDay, lowestDay;
	static double highTemps, highestTemp, lowTemps, lowestTemp = Double.MAX_VALUE;
	static double[][] temperatures;

	public static void main(String[] args) {
		temperatures = new double[N_DAYS][3];

		StringBuilder sb = new StringBuilder();
		try {
			readTemps(sb);
			sb.setLength(0);
			createReport(sb);
			System.out.print(sb);
			writeReport(sb);
		} catch (IOException e) {
			e.printStackTrace();
			// System.out.println("Error: " + e.getMessage());
		}
	}

	static void readTemps(StringBuilder sb) throws IOException {
		try (FileReader csvFile = new FileReader(CSV_PATH)) {
			int c = 0, i = 0, j = 0;
			while ((c = csvFile.read()) != -1) {
				if (c == ',') {
					temperatures[i][j++] = Double.parseDouble(sb.toString());
					sb.setLength(0);
					continue;
				}
				if (c == '\n') {
					temperatures[i][j] = Double.parseDouble(sb.toString());
					sb.setLength(0);
					highTemps += temperatures[i][1];
					lowTemps += temperatures[i][2];
					i++;
					j = 0;
					continue;
				}
				sb.append((char) c);
			}
		}
	}

	// create report and store it in sb
	static void createReport(StringBuilder sb) {
		sb.append(LINE);
		sb.append("December 2020: Temperatures in Utah\n");
		sb.append(LINE);
		sb.append("Day\tHigh\tLow\tVariance\n");
		sb.append(LINE);

		for (int i = 0; i < temperatures.length; i++) {
			if (temperatures[i][1] > highestTemp) {
				highestTemp = temperatures[i][1];
				highestDay = i;
			}
			if (temperatures[i][2] < lowestTemp) {
				lowestTemp = temperatures[i][2];
				lowestDay = i;
			}
			for (int j = 0; j < temperatures[i].length; j++) {
				// append day, high, low depending on value of j
				sb.append(String.format("%.0f", temperatures[i][j]));
				if (j < temperatures[i].length - 1)
					sb.append("\t");
				else {
					// append variance
					sb.append("\t");
					sb.append(String.format("%.0f", temperatures[i][j - 1] - temperatures[i][j]));
				}
			}
			sb.append("\n");
		}
		sb.append("December Highest Temperature: 12/" + temperatures[highestDay][0] + ": " + highestTemp
				+ " Average Hi: " + String.format("%.1f", highTemps / N_DAYS) + "\n");

		sb.append("December Lowest Temperature: 12/" + temperatures[lowestDay][0] + ": " + lowestTemp + " Average Lo: "
				+ String.format("%.1f", lowTemps / N_DAYS) + "\n");
		sb.append(LINE);
		sb.append("Graph\n");
		sb.append(SCALE_LABEL);
		for (int i = 0; i < temperatures.length; i++) {
			sb.append(String.format("%-2.0f", temperatures[i][0]) + " Hi "
					+ getRepeatedCharString('+', (int) temperatures[i][1]) + "\n");
			sb.append("   Lo " + getRepeatedCharString('-', (int) temperatures[i][2]) + "\n");
		}
		sb.append(SCALE_LABEL);
	}

	// write report to report file
	static void writeReport(StringBuilder sb) throws IOException {
		FileWriter writer = new FileWriter(new File(REPORT_PATH));
		writer.append(sb);
		writer.close();
	}

	// returns a string that contains the same character repeated nChars times
	static String getRepeatedCharString(char c, int nChars) {
		char[] chars = new char[nChars];
		for (int i = 0; i < chars.length; i++)
			chars[i] = c;
		return new String(chars);
	}
}