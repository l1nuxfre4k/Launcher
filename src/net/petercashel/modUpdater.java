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
    static String minecraftVersion;

    static String coremodDir = getPath() + "coremods" + File.separator;
    static String configDir = getPath() + "config" + File.separator;
    static String modDir = getPath() + "mods" + File.separator;
    static String binDir = getPath() + "bin" + File.separator;
    static String resourcesDir = getPath() + "resources" + File.separator;
    static String tmpDir = getPath() + "temp" + File.separator;
    static String jarDir = getPath() + "temp" + File.separator + "jar" + File.separator;
    static String fileInProgress = "";

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
	dir = new File(resourcesDir);

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

	if (getPlatform().ordinal() == 2) {
	    File f = new File(tmpDir + "7za.exe");
	    if (!f.exists()) {
		File f2 = new File(getJarDir() + "7za.exe");
		if (f.exists()) {
		    try {
			FileUtils.copyFile(f2, f);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	    if (!f.exists()) {
		try {
		    fileInProgress = "tools.csv";
		    downloadFile(launcherProperties.baseUrl + "/csv/tools.csv", tmpDir, "tools.csv");
		    CSVReader reader = new CSVReader(new FileReader(tmpDir + "tools.csv"));
		    String[] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
			if (!nextLine[0].startsWith("#")) {
			    System.out.println(nextLine[1]);
			    fileInProgress = nextLine[1];
			    downloadFile(nextLine[0], tmpDir, nextLine[1]);
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("Error During Download Of " + fileInProgress);

		}
	    }
	    // Check if file exists again
	    if (!f.exists()) {

		try {
		    downloadFile("https://dl.dropbox.com/u/13174207/PacasCraft/7za.exe", tmpDir, "7za.exe");

		} catch (IOException e1) {
		    e1.printStackTrace();
		    System.out.println("Error During Download Of " + fileInProgress);
		}
	    }
	}

	// Download Coremods
	GameUpdater.subtaskMessage = "";
	stateString = "Downloading Core Mods";
	GameUpdater.percentage = 20;
	FileUtils.deleteQuietly(new File(coremodDir));
	try {
	    fileInProgress = "coremods.csv";
	    downloadFile(launcherProperties.baseUrl + "/csv/coremods.csv", tmpDir, "coremods.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "coremods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[1];
		    System.out.println(nextLine[1]);
		    fileInProgress = nextLine[1];
		    downloadFile(nextLine[0], coremodDir, nextLine[1]);
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Downloading Mods";
	GameUpdater.percentage = 25;
	FileUtils.deleteQuietly(new File(modDir));
	// Download Mods
	try {
	    fileInProgress = "mods.csv";
	    downloadFile(launcherProperties.baseUrl + "/csv/mods.csv", tmpDir, "mods.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "mods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[1];
		    if (Integer.parseInt(nextLine[2]) != 2) {
			System.out.println(nextLine[1]);
			fileInProgress = nextLine[1];
			downloadFile(nextLine[0], modDir, nextLine[1]);
		    }
		    GameUpdater.percentage = GameUpdater.percentage + 1;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Checking minecraft.jar";
	GameUpdater.percentage = 70;
	// Download basemods + correct mc jar
	try {
	    // downloadFile(urlBase + "csv/version.csv",tmpDir,"version.csv");
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "version.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    fileInProgress = "minecraft additions";
		    downloadFile(("http://assets.minecraft.net/" + nextLine[0] + "/minecraft.jar"), binDir, "base_minecraft.jar");
		    modpackVersion = Integer.parseInt(nextLine[2]);
		    minecraftVersion = nextLine[0];
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Downloading basemods";
	GameUpdater.percentage = 75;
	try {
	    fileInProgress = "basemods.csv";
	    downloadFile(launcherProperties.baseUrl + "/csv/basemods.csv", tmpDir, "basemods.csv");
	    GameUpdater.percentage = 80;
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "basemods.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[1];
		    System.out.println(nextLine[1]);
		    fileInProgress = nextLine[1];
		    downloadFile(nextLine[0], binDir, nextLine[1]);
		    // downloadFile(nextLine[0], tmpDir, nextLine[1]);
		    GameUpdater.percentage = GameUpdater.percentage + 1;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	// Rebuild jar
	stateString = "Completing Installation...";
	GameUpdater.subtaskMessage = "Repacking minecraft.jar";
	GameUpdater.subtaskMessage = "Cleaning up";
	FileUtils.deleteQuietly(new File(jarDir));

	// Download config
	GameUpdater.subtaskMessage = "Downloading mod configs";
	GameUpdater.percentage = 90;
	try {
	    System.out.println("config.zip");
	    fileInProgress = "config.zip";
	    downloadFile(launcherProperties.baseUrl + "/config.zip", tmpDir, "config.zip");
	    unZip(tmpDir + "config.zip", configDir);
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	GameUpdater.subtaskMessage = "";
	stateString = "Downloading and Installing New Audio";
	GameUpdater.percentage = 75;
	try {
	    fileInProgress = "audio.csv";
	    downloadFile(launcherProperties.baseUrl + "/csv/audio.csv", tmpDir, "audio.csv");
	    GameUpdater.percentage = 91;
	    CSVReader reader = new CSVReader(new FileReader(tmpDir + "audio.csv"));
	    String[] nextLine;
	    while ((nextLine = reader.readNext()) != null) {
		if (!nextLine[0].startsWith("#")) {
		    GameUpdater.subtaskMessage = "Downloading " + nextLine[2];
		    System.out.println(nextLine[2]);
		    fileInProgress = nextLine[2];
		    downloadAudioFile(nextLine[0], resourcesDir, nextLine[1], nextLine[2]);
		    GameUpdater.percentage = GameUpdater.percentage + 1;
		}
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("Error During Download Of " + fileInProgress);
	    System.exit(1);
	}

	stateString = "";
	GameUpdater.subtaskMessage = "";
	try {
	    CSVWriter writer;
	    writer = new CSVWriter(new FileWriter(binDir + "version.csv"), ',');
	    String[] entries = new String[] { String.valueOf(modpackVersion), minecraftVersion };
	    writer.writeNext(entries);
	    writer.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	// feed in your array (or convert your data to an array)

    }

    private static String getJarDir() {
	String currentDir = new File(".").getAbsolutePath();
	System.out.println(currentDir);
	return currentDir;
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

    static void downloadAudioFile(String url, String dir, String subdir, String filename) throws IOException {
	URL URL;
	URL = new URL(url);
	File path = new File(dir + File.separator + subdir);

	if (!path.exists()) {
	    path.mkdirs();
	}
	File file = new File(path + File.separator + filename);
	org.apache.commons.io.FileUtils.copyURLToFile(URL, file);
    }

    static void downloadFile(String url, String dir, String filename) throws IOException {
	URL URL;
	URL = new URL(url);
	File File = new File(dir + filename);
	org.apache.commons.io.FileUtils.copyURLToFile(URL, File);
    }

    static String getPath() {
	// if (getPlatform().ordinal() == 3) {
	// return util.getWorkingDirectory() + File.separator +
	// "Library/Application Support/" + "minecraft/";
	// } else {
	return util.getWorkingDirectory() + File.separator;
	// }
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

    private static enum OS {
	linux, solaris, windows, macos, unknown;
    }

    private static OS getPlatform() {
	String osName = System.getProperty("os.name").toLowerCase();
	System.out.println(osName);
	if (osName.contains("win")) {
	    return OS.windows;
	}
	if (osName.contains("mac")) {
	    return OS.macos;
	}
	if (osName.contains("solaris")) {
	    return OS.solaris;
	}
	if (osName.contains("sunos")) {
	    return OS.solaris;
	}
	if (osName.contains("linux")) {
	    return OS.linux;
	}
	if (osName.contains("unix")) {
	    return OS.linux;
	}
	return OS.unknown;
    }

}