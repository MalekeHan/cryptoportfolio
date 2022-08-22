package cryptoportfolio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//@name Maleke Hanson
//@date/version 5/7/22

import java.util.HashMap;
import java.util.Map;

public class CryptoPortfolio {
	public static void main(String[] args) {

		final String filename = args[0];
		final String source = args[1];

		// Do not modify the code in the try/catch block below.
		try {
			HashMap<String, Double> counts = aggregateCounts(filename);
			Graph g = new Graph(counts);
			Map<String, Double> flows = g.findFlows(source);

			System.out.println("flows: " + flows);

		} catch (FileNotFoundException e) {
			System.out.println(filename + " does not exist");
			System.exit(1);
		}
	}

	/**
	 * aggregateCounts opens the csv file using the filename parameter and aggregate
	 * trade (transaction) counts over the same cryptocurrency Base Asset and Quote
	 * Asset pair.
	 *
	 * @param String filename represents the csv file name
	 * @returns HashMap<String, Double> list of "Base Asset, Quote Asset" and total
	 *          trade counts.
	 * @throws FileNotFoundException if the file does not exist.
	 */
	private static HashMap<String, Double> aggregateCounts(String filename) throws FileNotFoundException {
		final HashMap<String, Double> counts = new HashMap<>();
		final FileReader file = new FileReader(filename);
		final ArrayList<String> incomingTransaction = new ArrayList<>();
		final ArrayList<String> outgoingTransaction = new ArrayList<>();
		final ArrayList<Double> amountOfTrades = new ArrayList<>();
		final BufferedReader buffer = new BufferedReader(file);
		try {
			String newLine;
			buffer.readLine();
			String[] storage = new String[50];
			while ((newLine = buffer.readLine()) != null) {
				final StringBuilder crypto = new StringBuilder();
				storage = newLine.split(",");

				final Double tradeCount = Double.parseDouble(storage[storage.length - 1]);
				final String tradeRelationship = storage[3] + "-> " + storage[5];
				if(tradeCount == 0.0)
					continue;
				counts.merge(tradeRelationship, tradeCount, Double::sum);

			}

		} catch (final IOException e) {
			System.out.println(e);
		}

		return counts;
	}

}
