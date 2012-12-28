package net.petercashel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import au.com.bytecode.opencsv.CSVReader;
import net.minecraft.GameUpdater;
import net.minecraft.Util;

public class modUpdateCheck {
    private static Util util = new Util();
    public static String stateString = "Checking for modpack updates";
    static String tmpDir = modUpdater.getPath() + "temp" + File.separator;
    static String binDir = modUpdater.getPath() + "bin" + File.separator;
    static Boolean modpackNeedsUpdate = false;
    static Boolean repackNeeded = false;
    static int modpackVersion;
    static String minecraftVersion = "";

    public static void doUpdateCheck() {

	GameUpdater.subtaskMessage = "";
	stateString = "Checking for modpack updates";
	GameUpdater.percentage = 75;
	// Download basemods + correct mc jar
	if ((new File((binDir + "version.csv"))) != null) {

	    try {
		try {
		    CSVReader reader = new CSVReader(new FileReader(binDir + "version.csv"));
		    String[] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
			if (!nextLine[0].startsWith("#")) {
				modpackVersion = Integer.parseInt(nextLine[0]);
				if (!nextLine[1].isEmpty()) {
					minecraftVersion = nextLine[1];
				} else {
				    repackNeeded = true;
				}
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		CSVReader reader = new CSVReader(new FileReader(tmpDir + "version.csv"));
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
		    if (!nextLine[0].startsWith("#")) {
			if (Integer.parseInt(nextLine[2]) > modpackVersion) {
			modpackNeedsUpdate = true;
			}
			if (String.valueOf(nextLine[0]) != null) {
			    String mcVer = String.valueOf(nextLine[0]);
			    if (!mcVer.equals(minecraftVersion)) {
				repackNeeded = true;
			    }
			} else {
			    repackNeeded = true;
			}
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	} else {

	    modpackNeedsUpdate = true;

	}

	if (modpackNeedsUpdate == true) {
	    modUpdater.modUpdate = true;
	}
	if (repackNeeded == true) {
	    try {
		FileUtils.touch(new File((getJarDir() + "need.repack")));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public static String getJarDir() {

	// Dirty Cheats
	String currentDir = new File(".").getAbsolutePath();
	System.out.println(currentDir);
	return currentDir;

    }

}