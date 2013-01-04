package net.petercashel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import net.minecraft.GameUpdater;
import net.minecraft.Util;

public class launcherUpdater {
    private static Util util = new Util();
    public static String stateString = "Starting Launcher Updater";
    static String tmpDir = modUpdater.getPath() + "temp" + File.separator;
    public static Boolean launcherNeedsUpdate = false;

    // Hard Coded Launcher version
    private static int launcherVersion = 20121229;

    public static void doUpdateCheck() {
	GameUpdater.subtaskMessage = "";
	stateString = "Checking for launcher updates";
	GameUpdater.percentage = 05;
	// Download basemods + correct mc jar
	try {
	    modUpdater.downloadFile(launcherProperties.getProp("baseUrl") + "/csv/version.csv", tmpDir, "version.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "version.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    if (Integer.parseInt(nextLine[1]) != (launcherVersion)) {
			launcherNeedsUpdate = true;
		    }
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
	if (launcherNeedsUpdate == true) {
	    doUpdate();
	}

    }

    public static void doUpdate() {
	try {
	    modUpdater.downloadFile(launcherProperties.getProp("baseUrl") + "/autoupdate.jar", getJarDir(), "autoupdate.jar");
	    GameUpdater.subtaskMessage = "Restarting to Update Launcher";
	    try {
		Thread.sleep(6000);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    //PROCESS BUILDER TIME!
	    ProcessBuilder pb = new ProcessBuilder("java", "-classpath", "autoupdater.jar", "main");
	    Map<String, String> env = pb.environment();
	    pb.directory(new File(getJarDir()));
	    Process p = pb.start();
	    System.exit(0);

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    static String getJarDir() {

	String currentDir;
	try {
	    currentDir = new File(".").getCanonicalPath();
	    return currentDir + File.separator;
	} catch (IOException e) {
	    e.printStackTrace();
	    currentDir = new File(".").getAbsolutePath();
	}
	return currentDir + File.separator;

    }

}