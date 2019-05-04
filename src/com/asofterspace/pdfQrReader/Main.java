/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pdfQrReader;

import com.asofterspace.toolbox.barcodes.QrCode;
import com.asofterspace.toolbox.barcodes.QrCodeFactory;
import com.asofterspace.toolbox.io.BinaryFile;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.ImageFile;
import com.asofterspace.toolbox.io.PdfFile;
import com.asofterspace.toolbox.io.PdfObject;
import com.asofterspace.toolbox.io.PpmFile;
import com.asofterspace.toolbox.io.SimpleFile;
import com.asofterspace.toolbox.io.XmlElement;
import com.asofterspace.toolbox.utils.Image;
import com.asofterspace.toolbox.Utils;

import java.util.List;


public class Main {

	public final static String PROGRAM_TITLE = "PDF QR Reader";
	public final static String VERSION_NUMBER = "0.0.0.3(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "29. March 2019 - 4. May 2019";

	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		if (args.length > 0) {
			if (args[0].equals("version_for_zip")) {
				System.out.println("version " + Utils.getVersionNumber());
				return;
			}
		}

		PdfFile inputFile = new PdfFile("input.pdf");

		if (!inputFile.exists()) {
			System.out.println("No input found!");
			System.out.println("Please put a PDF into the folder of the PDF QR Reader and rename it to input.pdf.");
			return;
		}

		Directory targetDir = new Directory("data");
		targetDir.clear();

		List<File> imageFiles = inputFile.exportPictures(targetDir);

		SimpleFile lineOutput = new SimpleFile("output.txt");
		lineOutput.clearContent();

		SimpleFile jsonOutput = new SimpleFile("output.json");
		jsonOutput.clearContent();
		jsonOutput.appendContent("{");
		jsonOutput.appendContent("  \"qrcodes\": [");

		SimpleFile xmlOutput = new SimpleFile("output.xml");
		xmlOutput.clearContent();
		xmlOutput.appendContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlOutput.appendContent("<qrcodes>");

		for (File imageFile : imageFiles) {

			Image img = ImageFile.readImageFromFile(imageFile);

			System.out.println("Attempting to read QR Code from " + imageFile.getLocalFilename());

			QrCode code = QrCodeFactory.readFromSomewhereInImage(img);

			if (code == null) {
				System.out.println("In this image, no QR code could be found.\n");
				continue;
			}

			String thisContent = code.getContent();

			System.out.println("QR code is: " + thisContent + "\n");

			lineOutput.appendContent(thisContent);

			// TODO :: escape "
			jsonOutput.appendContent("	\"" + thisContent + "\",");

			xmlOutput.appendContent("  <qrcode>" + XmlElement.xmlEscape(thisContent) + "</qrcode>");

			/*
			PpmFile debugFile = new PpmFile("data/qr" + imageFile.getLocalFilename() + ".ppm");
			debugFile.assign(	code.getDatapointsAsImage());
			debugFile.save();
			*/
		}

		String jsonContent = jsonOutput.getContent();
		if (jsonContent.endsWith(",")) {
			jsonOutput.setContent(jsonContent.substring(0, jsonContent.length() - 1));
		}

		lineOutput.saveWithSystemLineEndings();

		jsonOutput.appendContent("  ]");
		jsonOutput.appendContent("}");
		jsonOutput.saveWithSystemLineEndings();

		xmlOutput.appendContent("</qrcodes>");
		xmlOutput.saveWithSystemLineEndings();
	}

}
