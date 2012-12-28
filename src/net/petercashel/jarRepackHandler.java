package net.petercashel;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import net.minecraft.Util;

public class jarRepackHandler {
    private static Util util = new Util();

    public static void repackChecker() {
	File f = new File(getJarDir() + File.separator + ".need.repack");
	if (f.exists()) {
	    switch (getPlatform().ordinal()) {
		case 0:
		case 1:
		    System.out.println(getPlatform().ordinal());
		    // Linux Code
		    break;
		case 2:
		    System.out.println(getPlatform().ordinal());
		    try {
			downloadFile("http://mcupdate.petercashel.net/WinRepackJar.bat", getJarDir() + File.separator, "WinRepackJar.bat");
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    // Win Code
		    break;
		case 3:
		    System.out.println(getPlatform().ordinal());
		    try {
			downloadFile("http://mcupdate.petercashel.net/MacRepackJar.cmd", getJarDir() + File.separator, "MacRepackJar.cmd");
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    // Mac Code
		    break;
		default:
	    }
	    repackRun();
	    FileUtils.deleteQuietly(new File(getJarDir() + File.separator + ".needs.repack"));
	}
    }

    static void repackRun() {
	repackRun(getJarDir());
    }

    static void repackRun(String jarDir) {

	switch (getPlatform().ordinal()) {
	    case 0:
	    case 1:
		System.out.println(getPlatform().ordinal());
		// Linux Code
		break;
	    case 2:
		System.out.println(getPlatform().ordinal());
		// Win Code
		try {
		    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start /WAIT /D " + getJarDir() + " WinRepackJar.bat");
		    Map<String, String> env = pb.environment();
		    pb.directory(new File(jarDir));
		    Process p = pb.start();
		    try {
			String response = convertStreamToStr(p.getInputStream());
			p.waitFor();

		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		FileUtils.deleteQuietly(new File(getJarDir() + File.separator + ".need.repack"));
		break;
	    case 3:
		System.out.println(getPlatform().ordinal());
		try {
		    ProcessBuilder pb = new ProcessBuilder("./MacRepackJar.cmd");
		    Map<String, String> env = pb.environment();
		    pb.directory(new File(jarDir));
		    Process p = pb.start();
		    try {
			String response = convertStreamToStr(p.getInputStream());
			p.waitFor();
			Thread.sleep(5000);

		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		// Mac Code
		FileUtils.deleteQuietly(new File(getJarDir() + File.separator + ".need.repack"));
		break;
	    default:
	}

    }

    static String getPath() {
	return util.getWorkingDirectory() + File.separator;
    }

    static void downloadFile(String url, String dir, String filename) throws IOException {
	URL URL;
	URL = new URL(url);
	File File = new File(dir + filename);
	org.apache.commons.io.FileUtils.copyURLToFile(URL, File);
    }

    static String getJarDir() {

	String currentDir = new File(".").getAbsolutePath();
	System.out.println(currentDir);
	return currentDir;

    }
    
    public String trimLastChar(String str) {

	  if (str.length() > 0 && str.charAt(str.length()-1)=='x') {
	    str = str.substring(0, str.length()-1);
	    return str;
	  }
	  else {
	    return str;
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

    /*
     * To convert the InputStream to String we use the Reader.read(char[]
     * buffer) method. We iterate until the Reader return -1 which means there's
     * no more data to read. We use the StringWriter class to produce the
     * string.
     */

    public static String convertStreamToStr(InputStream is) throws IOException {

	if (is != null) {
	    Writer writer = new StringWriter();

	    char[] buffer = new char[1024];
	    try {
		Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		    writer.write(buffer, 0, n);
		}
	    } finally {
		is.close();
	    }
	    return writer.toString();
	} else {
	    return "";
	}
    }

}
