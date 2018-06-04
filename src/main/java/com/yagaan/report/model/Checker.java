package com.yagaan.report.model;

/**
 * A checker is the rule used by the scanner to detect some issues into the
 * scanned application.
 * 
 * @author antoine
 *
 */
public class Checker {
	private String name;
	private String description;
	private Severity severity;
	private String customSeverity;
	private Classification classification;

	public Checker(String name) {
		super();
		this.name = name;
	}
	

	public void setCustomSeverity(String customSeverity) {
		this.customSeverity = customSeverity;
	}
	
	
	public String getCustomSeverity() {
		return customSeverity;
	}
	
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public Checker severity(Severity severity) {
		this.severity = severity;
		return this;
	}
	
	public Checker customSeverity(String severity) {
		this.customSeverity = severity;
		return this;
	}


	public Checker description(String desc) {
		this.description = desc;
		return this;
	}

	public Checker classification(Classification classification) {
		this.classification = classification;
		return this;
	}

	/**
	 * Get a description/documentation about the checker.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the name of the checker
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get some classification informations about the issues detected by this
	 * checker.
	 * 
	 * @return
	 */
	public Classification getClassification() {
		return classification;
	}

	@Override
	public String toString() {
		return name;
	}
}
