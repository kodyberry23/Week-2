package javaReview3;

import java.sql.*;
import java.util.Scanner;

/**
 * This assignment is an extension of the Java Review 2 assignment. In this
 * assignment you are asked to modify your Review 2 assignment code to obtain
 * the data for the temperatures report from a database instead of a csv file.
 * 
 * @author Kody Berry
 *
 */
public class PracticeDBDemo {
	// constants
	static final String REPORT_PATH = "TemperaturesReport.txt";
	static final String SCALE_LABEL = "      1   5    10   15   20   25   30   35   40   45   50\n"
			+ "      |   |    |    |    |    |    |    |    |    |    |\n";
	static final int N_DAYS = 31;

	static int highestDay, lowestDay;
	static double highTemps, highestTemp, lowTemps, lowestTemp = Double.MAX_VALUE;
	static String monthSelection;

	public static void main(String[] args) {
		// Your database connection information may be different depending on
		// your MySQL installation and the dbLogin and dbPassword you choose
		// to use in your database.
		String connectionString = "jdbc:mysql://localhost:3306/practice";
		String dbLogin = "root";
		String dbPassword = "password";
		Connection conn = null;

		Scanner myObj = new Scanner(System.in);

		System.out.println("Please Select a month by number:\n" + "11. November\n" + "12. December");
		String selection = myObj.next();

		String sql = "SELECT month, day, year, hi, lo FROM temperatures " + "WHERE month = " + selection
				+ " AND year = 2020 ORDER BY month, day, year;";
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();

		try {
			conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
			if (conn != null) {
				try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sql)) {
					int numRows;
					int numCols = 5;
					rs.last();
					numRows = rs.getRow();
					System.out.printf("Number of Records: %d%n", numRows);
					rs.first();
					String[][] dbResults = new String[numRows][numCols];
					;
					for (int i = 0; i < numRows; i++) {
						dbResults[i][0] = rs.getString("month");
						dbResults[i][1] = rs.getString("day");
						dbResults[i][2] = rs.getString("year");
						dbResults[i][3] = rs.getString("hi");
						dbResults[i][4] = rs.getString("lo");
						highTemps += Integer.parseInt(dbResults[i][3]);
						lowTemps += Integer.parseInt(dbResults[i][4]);
						rs.next();
					}
					System.out.printf("Number of Array Rows: %d%n", dbResults.length);
					printLine(60);
					if (Integer.parseInt(selection) == 11) {
						System.out.println("November 2020: Temperatures in Utah");
					} else {
						System.out.println("December 2020: Temperatures in Utah");
					}
					printLine(60);
					System.out.println("Date\t\tHi\tLo\tVariance");
					printLine(60);
					for (int i = 0; i < dbResults.length; i++) {
						System.out.printf("%s/%s/%s\t%s\t%s\t%s%n", dbResults[i][0], dbResults[i][1], dbResults[i][2],
								dbResults[i][3], dbResults[i][4],
								Integer.parseInt(dbResults[i][3]) - Integer.parseInt(dbResults[i][4]));
					}
					printLine(60);
					for (int i = 0; i < dbResults.length; i++) {
						if (Integer.parseInt(dbResults[i][3]) > highestTemp) {
							highestTemp = Integer.parseInt(dbResults[i][3]);
							highestDay = i;
						}
						if (Integer.parseInt(dbResults[i][4]) < lowestTemp) {
							lowestTemp = Integer.parseInt(dbResults[i][4]);
							lowestDay = i;
						}

					}
					if (Integer.parseInt(selection) == 11) {
						sb.append("November Highest Temperature: " + selection + "/" + dbResults[highestDay][1] + ": "
								+ highestTemp + " Average Hi: " + String.format("%.1f", highTemps / N_DAYS) + "\n");

						sb.append("November Lowest Temperature: " + selection + "/" + dbResults[lowestDay][1] + ": "
								+ lowestTemp + " Average Lo: " + String.format("%.1f", lowTemps / N_DAYS) + "\n");
					} else {
						sb.append("December Highest Temperature: " + selection + "/" + dbResults[highestDay][1] + ": "
								+ highestTemp + " Average Hi: " + String.format("%.1f", highTemps / N_DAYS) + "\n");

						sb.append("December Lowest Temperature: " + selection + "/" + dbResults[lowestDay][1] + ": "
								+ lowestTemp + " Average Lo: " + String.format("%.1f", lowTemps / N_DAYS) + "\n");
					}
					System.out.print(sb);

					printLine(60);
					System.out.println("Graph");
					printLine(60);
					System.out.println(SCALE_LABEL);
					printLine(60);
					for (int i = 0; i < dbResults.length; i++) {
						sb1.append(String.format("%-2.0f", Float.parseFloat(dbResults[i][1])) + " Hi "
								+ getRepeatedCharString('+', (int) Integer.parseInt(dbResults[i][3])) + "\n");
						sb1.append(
								"   Lo " + getRepeatedCharString('-', (int) Integer.parseInt(dbResults[i][4])) + "\n");
					}
					System.out.println(sb1);
					printLine(60);
					System.out.println(SCALE_LABEL);
					printLine(60);
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("Database connection failed.");
			e.printStackTrace();
		}

	}

	// Method to print lines in the console as a separator
	private static void printLine(int dashes) {
		for (int i = 1; i <= dashes; i++) {
			System.out.print("-");
		}
		System.out.print("\n");
	}

	// returns a string that contains the same character repeated nChars times
	static String getRepeatedCharString(char c, int nChars) {
		char[] chars = new char[nChars];
		for (int i = 0; i < chars.length; i++)
			chars[i] = c;
		return new String(chars);
	}

}
