package net.petercashel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import net.minecraft.GameUpdater;
import net.minecraft.Util;

public class modUpdater {
    private static Util util = new Util();
    public static boolean modUpdate = false;
    public static String username = "";
    public static String stateString = "Starting Mod Updater";
    static int modpackVersion = 0;

    static String coremodDir = getPath() + "coremods" + File.separator;
    static String configDir = getPath() + "config" + File.separator;
    static String modDir = getPath() + "mods" + File.separator;
    static String binDir = getPath() + "bin" + File.separator;
    static String tmpDir = getPath() + "temp" + File.separator;
    static String jarDir = getPath() + "temp" + File.separator + "jar" + File.separator;

    public static void run() {
	System.out.println("Mod Updater Starting");
	GameUpdater.percentage = 10;
	GameUpdater.subtaskMessage = "";

	// Make sure the account is premium, close if not.
	// Forge ML does not work on demo versions.
	try {
	    URL premiumURL = new URL("https://minecraft.net/haspaid.jsp?user=" + username);
	    String premiumStatus = getContentResult(premiumURL);
	    boolean isPremium = premiumStatus.toLowerCase().contains("true");
	    if (!isPremium) {
		System.out.println("NOT PREMIUM!");
		System.exit(0);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	// Make sure Directories exist
	FileUtils.deleteQuietly(new File(coremodDir));
	File dir = new File(coremodDir);
	if (!dir.exists()) {
	    dir.mkdirs();
	}
	dir = new File(configDir);

	if (!dir.exists()) {
	    dir.mkdirs();
	}
	FileUtils.deleteQuietly(new File(modDir));
	dir = new File(modDir);

	if (!dir.exists()) {
	    dir.mkdirs();
	}
	dir = new File(binDir);

	if (!dir.exists()) {
	    dir.mkdirs();
	}
	dir = new File(tmpDir);

	if (!dir.exists()) {
	    dir.mkdirs();
	}
	dir = new File(tmpDir + "jar" + File.separator);
	if (!dir.exists()) {
	    dir.mkdirs();
	}

	// Update Code Here

	// Grab some tools silently
	try {
	    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/tools.csv", tmpDir, "tools.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "tools.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    downloadFile(nextLine[0], tmpDir, nextLine[1]);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Download Coremods
	GameUpdater.subtaskMessage = "";
	stateString = "Downloading Core Mods";
	GameUpdater.percentage = 20;
	FileUtils.deleteQuietly(new File(coremodDir));
	try {
	    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/coremods.csv", tmpDir, "coremods.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "coremods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[1];
		    downloadFile(nextLine[0], coremodDir, nextLine[1]);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Downloading Mods";
	GameUpdater.percentage = 25;
	FileUtils.deleteQuietly(new File(modDir));
	// Download Mods
	try {
	    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/mods.csv", tmpDir, "mods.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "mods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[1];
		    if (Integer.parseInt(nextLine[2]) != 2) {
			downloadFile(nextLine[0], modDir, nextLine[1]);
		    }
		    GameUpdater.percentage = GameUpdater.percentage + 1;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Checking minecraft.jar";
	GameUpdater.percentage = 70;
	// Download basemods + correct mc jar
	try {
	    // downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/version.csv",tmpDir,"version.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "version.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    downloadFile(("http://assets.minecraft.net/" + nextLine[0] + "/minecraft.jar"), binDir, "base_minecraft.jar");
		    modpackVersion = Integer.parseInt(nextLine[1]);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Downloading and Installing basemods";
	GameUpdater.percentage = 75;
	try {
	    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/basemods.csv", tmpDir, "basemods.csv");
	    GameUpdater.percentage = 80;
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "basemods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Extracting " + nextLine[1];
		    downloadFile(nextLine[0], binDir, nextLine[1]);
		    downloadFile(nextLine[0], tmpDir, nextLine[1]);
		    unZip((tmpDir + nextLine[1]), jarDir);
		    GameUpdater.percentage = GameUpdater.percentage + 1;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// Rebuild jar
	stateString = "Completing Installation...";
	GameUpdater.subtaskMessage = "Repacking minecraft.jar";
	repackJar(binDir, tmpDir);
	GameUpdater.subtaskMessage = "Cleaning up";
	FileUtils.deleteQuietly(new File(jarDir));

	// Download config
	GameUpdater.subtaskMessage = "Downloading mod configs";
	GameUpdater.percentage = 90;
	try {
	    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/config.zip", tmpDir, "config.zip");
	    unZip(tmpDir + "config.zip", configDir);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	stateString = "";
	GameUpdater.subtaskMessage = "";
	try {
	    CSVWriter writer;
	    writer = new CSVWriter(new FileWriter(binDir + "version.csv"), ',');
	    String[] entries = new String[] { String.valueOf(modpackVersion) };
	    writer.writeNext(entries);
	    writer.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	// feed in your array (or convert your data to an array)

    }

    private static String getContentResult(URL url) throws IOException {

	InputStream in = url.openStream();
	StringBuffer sb = new StringBuffer();

	byte[] buffer = new byte[256];

	while (true) {
	    int byteRead = in.read(buffer);
	    if (byteRead == -1)
		break;
	    for (int i = 0; i < byteRead; i++) {
		sb.append((char) buffer[i]);
	    }
	}
	return sb.toString();
    }

    static void downloadFile(String url, String dir, String filename) throws IOException {
	URL URL;
	URL = new URL(url);
	File File = new File(dir + filename);
	org.apache.commons.io.FileUtils.copyURLToFile(URL, File);
    }

    static String getPath() {
	return util.getWorkingDirectory() + File.separator;
    }

    static public void unZip(String zipFile, String outputFolder) throws ZipException, IOException {
	System.out.println(zipFile);
	int BUFFER = 2048;
	File file = new File(zipFile);

	ZipFile zip = new ZipFile(file);
	// String newPath = zipFile.substring(0, zipFile.length() - 4);
	String newPath = outputFolder;

	new File(newPath).mkdir();
	Enumeration zipFileEntries = zip.entries();

	// Process each entry
	while (zipFileEntries.hasMoreElements()) {
	    // grab a zip file entry
	    ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	    String currentEntry = entry.getName();
	    File destFile = new File(newPath, currentEntry);
	    // destFile = new File(newPath, destFile.getName());
	    File destinationParent = destFile.getParentFile();

	    // create the parent directory structure if needed
	    destinationParent.mkdirs();

	    if (!entry.isDirectory()) {
		BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
		int currentByte;
		// establish buffer for writing file
		byte data[] = new byte[BUFFER];

		// write the current file to disk
		FileOutputStream fos = new FileOutputStream(destFile);
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

		// read and write until last byte is encountered
		while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
		    dest.write(data, 0, currentByte);
		}
		dest.flush();
		dest.close();
		is.close();
	    }

	    if (currentEntry.endsWith(".zip")) {
		// found a zip file, try to open
		unZip(destFile.getAbsolutePath(), outputFolder);
	    }
	}
    }

    private static void repackJar(String bindir, String tempdir) {

	// Not Coded. Need Solution.

	try {
	    String osName = System.getProperty("os.name");
	    // System.out.println("Your OS is Detected As");
	    // System.out.println(osName);
	    if (osName.startsWith("Mac OS")) {
		// Need to test on mac

	    } else if (osName.startsWith("Windows")) {
		// Process process = Runtime.getRuntime().exec("repackJar.bat");
		// process.waitFor();

	    } else {
		// Need to test on linux
	    }
	} catch (Exception e) {
	}

    }

}