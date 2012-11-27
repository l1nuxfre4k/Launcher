package net.petercashel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import net.minecraft.GameUpdater;
import net.minecraft.Util;

public class launcherUpdater {
	private static Util util = new Util();
	public static String stateString = "Starting Launcher Updater";
	static String tmpDir = modUpdater.getPath() + "temp" + File.separator;
	public static Boolean launcherNeedsUpdate = false;
	
	//Hard Coded Launcher version
	private static int launcherVersion = 20121127;
	
	public static void doUpdateCheck() {
		System.out.println(launcherNeedsUpdate);
		GameUpdater.subtaskMessage = "";
		stateString = "Checking for launcher updates";
		GameUpdater.percentage = 70;
		//Download basemods + correct mc jar
		try {
			modUpdater.downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/version.csv",tmpDir,"version.csv");
			CSVReader reader = new CSVReader(new FileReader(tmpDir + "version.csv"));
		    String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		    //    downloadFile(( "http://assets.minecraft.net/" + nextLine[0] + "/minecraft.jar" ),binDir,"base_minecraft.jar");
		    	System.out.println(launcherVersion);
		    	System.out.println(nextLine[1]);
		    	if (Integer.parseInt(nextLine[1]) != (launcherVersion)) {
		    	launcherNeedsUpdate = true;
		    }
		    }
	    } catch (IOException e) {
				e.printStackTrace();
			}
		System.out.println(launcherNeedsUpdate);
		if (launcherNeedsUpdate == true) {
			System.out.println(launcherNeedsUpdate);
			doUpdate();
		}
		
	}
	
	public static void doUpdate() {
	try {
		modUpdater.downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/Launcher.jar", getJarDir(),"New_Launcher.jar");
			GameUpdater.subtaskMessage = "Replace Launcher.jar with New_Launcher.jar";
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.exit(0);	
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	
	
	public static String getJarDir() {
		
		//Dirty Cheats
		String currentDir = new File(".").getAbsolutePath();
		System.out.println(currentDir);
		return currentDir;
				
	}

	
}