import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import net.petercashel.launcherProperties;

// This class handles the OS Specific varible setting and what not.
// Designed to replace the launcher scripts.

// Class to reinitalise to is net.minecraft.LauncherFrame

public class main {

    public static void main(String[] args) {

	// Implement AutoUpdate here.

	// OS Specific relaunch stuff now.
	// launcherProperties.initProps();
	try {
	    FileUtils.forceMkdir(new File(getJarDir() + launcherProperties.installFolder));
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	try {

	    ProcessBuilder pb = new ProcessBuilder("java", "-classpath", "Launcher.jar", "net.minecraft.MinecraftLauncher");
	    Map<String, String> env = pb.environment();
	    env.put("APPDATA", (getJarDir() + launcherProperties.installFolder));
	    switch (getPlatform().ordinal()) {
		case 0:
		case 1:
		    // Linux Code
		    env.put("HOME", (getJarDir() + launcherProperties.installFolder));
		    env.put("user.home", (getJarDir() + launcherProperties.installFolder));
		    break;
		case 2:
		    // Win Code
		    break;
		case 3:
		    // Mac Code
		    env.put("user.home", (getJarDir() + launcherProperties.installFolder));
		    env.put("HOME", (getJarDir() + launcherProperties.installFolder));
		    break;
		default:
	    }
	    pb.directory(new File(getJarDir()));
	    Process p = pb.start();
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
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    currentDir = new File(".").getAbsolutePath();
	}
	return currentDir + File.separator;

    }

    private static enum OS {
	linux, solaris, windows, macos, unknown;
    }

    private static OS getPlatform() {
	String osName = System.getProperty("os.name").toLowerCase();
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