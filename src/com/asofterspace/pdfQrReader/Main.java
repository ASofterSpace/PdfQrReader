/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.pdfQrReader;

import com.asofterspace.toolbox.barcodes.QrCode;
import com.asofterspace.toolbox.io.BinaryFile;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.PdfFile;
import com.asofterspace.toolbox.io.PdfObject;
import com.asofterspace.toolbox.io.PpmFile;
import com.asofterspace.toolbox.io.SimpleFile;
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

		inputFile.exportPictures(targetDir);

		SimpleFile lineOutput = new SimpleFile("output.txt");
		lineOutput.clearContent();

		PpmFile ppm = new PpmFile("data/Image4.ppm");

		// great!
		// now try to read out a version 3 QR code...
		// for now, let's just hardcode the QR code location:
		// it starts at 1175, 4
		// it is rotated left - but our fancy QrCode should automatically notice that! ^^
		// it is a bit larger than one pixel per QR-pixel, but not quite two pixels per QR-pixel
		int offsetX = 1175;
		int offsetY = 4;
		int enlargeX = 0;
		int enlargeY = 0;
		QrCode code = new QrCode(3);
		for (int x = 0; x < code.getWidth(); x++) {
			for (int y = 0; y < code.getHeight(); y++) {
				code.setDatapoint(x, y, ppm.getPixel(offsetX + x + enlargeX, offsetY + y + enlargeY).isDark());
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

		lineOutput.appendContent(thisContent);

		lineOutput.save();
	}

}
