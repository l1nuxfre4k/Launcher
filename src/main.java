import java.io.File;
import java.io.IOException;
import java.util.Map;



// This class handles the OS Specific varible setting and what not.
// Designed to replace the launcher scripts.

// Class to reinitalise to is net.minecraft.LauncherFrame



public class main {
    
    
    
    public static void main(String[] args) {
	
	//Implement AutoUpdate here.
	
	
	// OS Specific relaunch stuff now.
	System.out.println("Main Has Run");
	try {
	
	     ProcessBuilder pb = new ProcessBuilder("java", "-classpath", "Launcher.jar", "net.minecraft.MinecraftLauncher");
	     Map<String, String> env = pb.environment();
	     env.put("APPDATA", (getJarDir() + File.separator + "PacasLand"));
	     switch (getPlatform().ordinal()) {
			case 0:
			case 1:
			    System.out.println(getPlatform().ordinal());
				//Linux Code
			    	env.put("HOME", (getJarDir() + File.separator + "PacasLand"));
			    	env.put("user.home", (getJarDir() + File.separator + "PacasLand"));
			    	break;
			case 2:
			    System.out.println(getPlatform().ordinal());
				//Win Code
			    	break;
			case 3:
			    System.out.println(getPlatform().ordinal());
				//Mac Code
			    	env.put("user.home", (getJarDir() + File.separator + "PacasLand"));
				env.put("HOME", (getJarDir() + File.separator + "PacasLand"));
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
	
	String currentDir = new File(".").getAbsolutePath();
	System.out.println(currentDir);
	return currentDir;
			
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