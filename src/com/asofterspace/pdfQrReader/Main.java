/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pdfQrReader;

import com.asofterspace.toolbox.barcodes.QrCode;
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
	public final static String VERSION_NUMBER = "0.0.0.1(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "29. March 2019";

	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		PdfFile inputFile = new PdfFile("input.pdf");

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

			// great!
			// now try to read out a version 3 QR code...
			// for now, let's just hardcode the QR code location:
			// go to a column that is right-wards of the logo at the left...
			// row four...
			// advance to the right until you get a dark one!
			int offsetX3 = 300;
			for (; offsetX3 < img.getWidth(); offsetX3++) {
				if (img.getPixel(offsetX3, 3).isDark()) {
					break;
				}
			}
			int offsetX4 = 300;
			for (; offsetX4 < img.getWidth(); offsetX4++) {
				if (img.getPixel(offsetX4, 4).isDark()) {
					break;
				}
			}
			int offsetX5 = 300;
			for (; offsetX5 < img.getWidth(); offsetX5++) {
				if (img.getPixel(offsetX5, 5).isDark()) {
					break;
				}
			}

			int offsetY = 3;
			int offsetX = offsetX3;

			if (offsetX4 < offsetX) {
				offsetY = 4;
				offsetX = offsetX4;
			}

			if (offsetX5 < offsetX) {
				offsetY = 5;
				offsetX = offsetX5;
			}

			System.out.println("Reading QR Code from " + imageFile.getLocalFilename() + " at [" + img.getWidth() + " x " + offsetX + "]");

			int enlargeX = 0;
			int enlargeY = 0;
			QrCode code = new QrCode(3);
			for (int x = 0; x < code.getWidth(); x++) {
				for (int y = 0; y < code.getHeight(); y++) {
					code.setDatapoint(x, y, img.getPixel(offsetX + x + enlargeX, offsetY + y + enlargeY).isDark());
					if (y % 4 != 3) {
						enlargeY++;
					}
				}
				if (x % 4 != 3) {
					enlargeX++;
				}
				enlargeY = 0;
			}

			String thisContent = code.getContent();

			if ((thisContent == null) || thisContent.equals("")) {
				System.out.println("This image contains no QR code!\n");
			} else {
				System.out.println("QR code is: " + thisContent + "\n");

				lineOutput.appendContent(thisContent);

				// TODO :: escape "
				jsonOutput.appendContent("	\"" + thisContent + "\",");

				xmlOutput.appendContent("  <qrcode>" + XmlElement.xmlEscape(thisContent) + "</qrcode>");
			}

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
