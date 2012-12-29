package net.minecraft;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;

import net.minecraft.LoginForm;

public class Util {
    /*
     * private File workDir = null;
     * 
     * public File getWorkingDirectory() { if (workDir == null) workDir =
     * getWorkingDirectory(File.separator + LoginForm.mcdir + File.separator +
     * "minecraft"); return workDir;
     * 
     * }
     */

    public File getWorkingDirectory() {
	String userHome = LoginForm.mcFolder.toString();
	File workingDirectory;
	String applicationName = File.separator + ".minecraft";
	// System.out.println(getPlatform().ordinal());
	switch (getPlatform().ordinal()) {
	    case 0:
	    case 1:
		workingDirectory = new File(userHome, applicationName + '/');
		break;
	    case 2:
		String currentDir = System.getProperty("user.dir");
		String applicationData = System.getenv("APPDATA");
		if (applicationData != null)
		    workingDirectory = new File(applicationData, applicationName + '/');
		else
		    workingDirectory = new File(userHome, applicationName + '/');
		break;
	    case 3:
		applicationName = File.separator + "minecraft";
		workingDirectory = new File(userHome, LoginForm.mcdir + File.separator + "Library/Application Support/" + applicationName);
		break;
	    default:
		workingDirectory = new File(userHome, applicationName + '/');
	}
	if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
	    throw new RuntimeException("The working directory could not be created: " + workingDirectory);
	return workingDirectory;
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

    public static String excutePost(String targetURL, String urlParameters) {
	HttpsURLConnection connection = null;
	try {
	    URL url = new URL(targetURL);
	    connection = (HttpsURLConnection) url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

	    connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
	    connection.setRequestProperty("Content-Language", "en-US");

	    connection.setUseCaches(false);
	    connection.setDoInput(true);
	    connection.setDoOutput(true);

	    connection.connect();
	    Certificate[] certs = connection.getServerCertificates();

	    byte[] bytes = new byte[294];
	    DataInputStream dis = new DataInputStream(Util.class.getResourceAsStream("minecraft.key"));
	    dis.readFully(bytes);
	    dis.close();

	    Certificate c = certs[0];
	    PublicKey pk = c.getPublicKey();
	    byte[] data = pk.getEncoded();

	    for (int i = 0; i < data.length; i++) {
		if (data[i] == bytes[i])
		    continue;
		throw new RuntimeException("Public key mismatch");
	    }

	    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.flush();
	    wr.close();

	    InputStream is = connection.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    StringBuffer response = new StringBuffer();
	    String line;
	    while ((line = rd.readLine()) != null) {
		response.append(line);
		response.append('\r');
	    }
	    rd.close();

	    String str1 = response.toString();
	    return str1;
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	} finally {
	    if (connection != null)
		connection.disconnect();
	}
    }

    public static boolean isEmpty(String str) {
	return (str == null) || (str.length() == 0);
    }

    static final String[] browsers = { "nautilus", "dolphin", "thunar", "pcmanfm", "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror",
	    "midori", "kazehakase", "mozilla" };
    static final String errMsg = "Error attempting to launch web browser";

    public static void openURL(String url) {
	try { // attempt to use Desktop library from JDK 1.6+
	    Class<?> d = Class.forName("java.awt.Desktop");
	    d.getDeclaredMethod("browse", new Class[] { java.net.URI.class }).invoke(d.getDeclaredMethod("getDesktop").invoke(null),
		    new Object[] { java.net.URI.create(url) });
	} catch (Exception ignore) { // library not available or failed
	    String osName = System.getProperty("os.name");
	    try {
		if (osName.startsWith("Mac OS")) {
		    Class.forName("com.apple.eio.FileManager").getDeclaredMethod("openURL", new Class[] { String.class }).invoke(null, new Object[] { url });
		} else if (osName.startsWith("Windows"))
		    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		else { // assume Unix or Linux
		    String browser = null;
		    for (String b : browsers)
			if (browser == null && Runtime.getRuntime().exec(new String[] { "which", b }).getInputStream().read() != -1)
			    Runtime.getRuntime().exec(new String[] { browser = b, url });
		    if (browser == null)
			throw new Exception(Arrays.toString(browsers));
		}
	    } catch (Exception e) {
		JOptionPane.showMessageDialog(null, errMsg + "\n" + e.toString());
	    }
	}
    }

    private static enum OS {
	linux, solaris, windows, macos, unknown;
    }
}