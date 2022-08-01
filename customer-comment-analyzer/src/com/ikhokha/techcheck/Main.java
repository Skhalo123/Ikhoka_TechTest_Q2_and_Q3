package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) {
		try {
			
			// get files from docs folder
			File[] files = new File("docs").listFiles((file, s) -> s.endsWith(".txt"));
			
			// configurable threads
			int Thread = files.length < 10 ? files.length : files.length / 3;
			
			// create thread pool.
			ExecutorService executor = Executors.newFixedThreadPool(Thread);
			
			// create stack of future 
			Stack<Future<Report>> futureReport = new Stack<>();
			ArrayList<Report> reports = new ArrayList<>();
			
			// iterate over file, analysing each
			for (File file : files) {
				var c = new CommentAnalyzer(file);
				futureReport.push(executor.submit(c.analyze));
			}
			
			// while the stack has results to be processed
			while (!futureReport.empty()) {
				
				// get/pop comment future
				var result = futureReport.pop();
				
				// get the future
				var comment = result.get();
				
				// add comment to report
				reports.add(comment);
			}
			executor.shutdown();
			printReportResult(reports);
		} catch (ExecutionException | InterruptedException e) {
			System.out.println("Thread execution error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Consolidates and prints a report of each analysis
	 * @param reports populated list of reports per comment file
	 * */
	private static void printReportResult(ArrayList<Report> reports) {
		// print out comment report for current file
		// consolidate results
		Report report = new Report();
		for (int number = 0; number < reports.size(); number++) {
			report.shorterThan15 += reports.get(number).shorterThan15;
			report.Movers += reports.get(number).Movers;
			report.Shakers += reports.get(number).Shakers;
			report.Questions += reports.get(number).Questions;
			report.Spam += reports.get(number).Spam;
				
		}
		
		System.out.println("===================================");
		System.out.printf("SHORTER_THAN_15: %s%n", report.shorterThan15);
		System.out.printf("MOVERS_MENTIONS: %s%n", report.Movers);
		System.out.printf("SHAKERS_MENTIONS: %s%n", report.Shakers);
		System.out.printf("QUESTIONS: %s%n", report.Questions);
		System.out.printf("SPAM: %s%n", report.Spam);

		System.out.println("===================================");
	}

}
