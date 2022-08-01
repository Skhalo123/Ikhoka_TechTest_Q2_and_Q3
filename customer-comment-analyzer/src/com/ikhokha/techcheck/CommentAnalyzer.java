package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CommentAnalyzer {
	private File _file;
	public CommentAnalyzer(File file) {
		_file = file;
	}
	public Callable<Report> analyze = ()  -> {
		Report report = new Report();
		try {
			// read the file
			var FileReader = new BufferedReader(new FileReader(_file));
			
			// get lines in file, converting stream to strings
			var lines = FileReader.lines().collect(Collectors.toList());

			// filter the information you want
			long movers = lines.stream().filter(s -> s.toLowerCase().contains("mover")).count();
			long shakers = lines.stream().filter(s -> s.toLowerCase().contains("shaker")).count();
			long shorterThan15 = lines.stream().filter(s -> s.length() < 15).count();
			long questions = lines.stream().filter(s -> s.contains("?")).count();

			// match all urls and assign to spam count
			var path = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)", Pattern.CASE_INSENSITIVE);
			long spam = lines.stream().filter(s -> path.matcher(s).find()).count();

			// add results to report
			report.shorterThan15 = shorterThan15;
			report.Shakers = shakers;
			report.Movers = movers;
			report.Questions = questions;
			report.Spam = spam;
			System.out.println("File processed: " + _file.getName());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return report;
	};

}
