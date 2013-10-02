package com.nish.model;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CustomSAXParser extends DefaultHandler {
	private boolean streetFound, locationFound = false;
	private ArrayList<String> locations = new ArrayList<String>();

	public CustomSAXParser() {
		streetFound = true;
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (streetFound && qName.equalsIgnoreCase("formatted_address")) {
			locationFound = true;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (locationFound) {
			String bookName = new String(ch, start, length);
			locations.add(bookName);
			streetFound = locationFound = false;			
		}
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	public ArrayList<String> getLocations() {
		return locations;
	}

	public void setLocations(ArrayList<String> locations) {
		this.locations = locations;
	}
}
