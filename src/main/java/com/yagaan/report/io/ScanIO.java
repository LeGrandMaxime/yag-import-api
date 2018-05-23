package com.yagaan.report.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.yagaan.report.model.Checker;
import com.yagaan.report.model.Issue;
import com.yagaan.report.model.Scan;

public final class ScanIO {

	private static final String ISSUES = "issues";
	private static final String CHECKERS = "checkers";

	private ScanIO() {

	}

	/**
	 * Serialize a results objects as JSON to an output stream. Issues are obtained
	 * from a supplier that need to returns null in case of end of the supplied
	 * issues. Use this in case of large number of issues to reduce memory consumption.
	 * 
	 * @param output
	 * @param results
	 * @param issues
	 * @throws IOException
	 */
	public static void write(Scan results, Supplier<Issue> issues, OutputStream output) throws IOException {
		Gson gson = new Gson();
		JsonWriter writer = gson.newJsonWriter(new OutputStreamWriter(output));
		writer.beginObject();
		writer.name(Scan.class.getSimpleName()).value(results.getApplication());
		writer.name(CHECKERS);
		writer.beginArray();
		for (Checker checker : results.getCheckers()) {
			writer.jsonValue(gson.toJson(checker));
		}
		writer.endArray();
		writer.name(ISSUES);
		writer.beginArray();
		if (issues != null) {
			Issue issue = issues.get();
			while (issue != null) {
				writer.jsonValue(gson.toJson(issue));
				issue = issues.get();
			}
		}
		writer.endArray();
		writer.endObject();
		writer.close();
		output.close();
	}

	/**
	 * Serialize a scan results objects as JSON to an output stream. Issues are
	 * contained into a collection.
	 * 
	 * @param output
	 * @param results
	 * @param issues
	 * @throws IOException
	 */
	public static void write(Scan results, Collection<Issue> issues, OutputStream output) throws IOException {
		Iterator<Issue> iterator = issues.iterator();
		write(results, iterator, output);
	}

	public static void write(Scan results, Iterator<Issue> iterator, OutputStream output) throws IOException {
		Supplier<Issue> supplier = new Supplier<Issue>() {

			@Override
			public Issue get() {
				if (iterator.hasNext()) {
					return iterator.next();
				}
				return null;
			}
		};
		write(results, supplier, output);
	}

	public static void write(Scan results, OutputStream output, Issue... issues) throws IOException {
		Iterator<Issue> iterator = Arrays.asList(issues).iterator();
		write(results, iterator, output);
	}

	/**
	 * Read some scan results from a JSON input stream. Issues are added into a
	 * input collection. Prefer to use 'consume' in case of a large input content to
	 * reduce memory consumption.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static Scan read(InputStream input, Collection<Issue> issues) throws IOException {
		List<Scan> resultList = new ArrayList<Scan>();

		consume(input, (r) -> {
			resultList.add(r);
		}, (i) -> {
			issues.add(i);
		});
		if (resultList.size() == 1) {
			Scan results = resultList.get(0);
			return results;
		}
		throw new IllegalStateException("More than one scan in JSON.");
	}

	/**
	 * Consume a JSON export of some scan results.
	 * 
	 * @param input
	 * @param mainResultsConsumer
	 *            consumer of the main results entity (checkers and application
	 *            name)
	 * @param issuesConsumer
	 *            consumer of the issues
	 * @throws IOException
	 */
	public static void consume(InputStream input, Consumer<Scan> mainResultsConsumer, Consumer<Issue> issuesConsumer)
			throws IOException {
		Gson gson = new Gson();
		JsonReader reader = gson.newJsonReader(new InputStreamReader(input));
		reader.beginObject();
		reader.nextName();
		Scan results = new Scan(reader.nextName());

		List<Checker> checkers = gson.fromJson(reader, new ArrayList<Checker>().getClass());
		results.setCheckers(checkers);
		mainResultsConsumer.accept(results);
		reader.nextName();
		reader.beginArray();
		while (reader.hasNext()) {
			Issue issue = gson.fromJson(reader, Issue.class);
			issuesConsumer.accept(issue);
		}

		reader.endArray();

		reader.endObject();

	}
}
