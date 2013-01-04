package net.petercashel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class launcherProperties {

    // public static String baseUrl = "http://mcupdate.petercashel.net";
    // public static String installFolder = "PacasLand";

    // Default is "http://mcupdate.tumblr.com/"
    // public static String loginViewURL = "http://mcupdate.tumblr.com/";

    public static String getProp(String key) {

	Properties prop = new Properties();

	try {
	    // load a properties file from class path, inside static method

	    try {
		prop.load(new FileInputStream("config.properties"));

	    } catch (IOException ex) {
		ex.printStackTrace();
	    }

	    // get the property value and print it out
	    System.out.println(prop.getProperty("baseUrl"));
	    System.out.println(prop.getProperty("installFolder"));
	    System.out.println(prop.getProperty("loginViewURL"));

	    if (!prop.containsKey("baseUrl")) {
		prop.setProperty("baseUrl", "http://mcupdate.petercashel.net");
	    }
	    if (!prop.containsKey("installFolder")) {
		prop.setProperty("installFolder", "PacasLand");
	    }
	    if (!prop.containsKey("loginViewURL")) {
		prop.setProperty("loginViewURL", "http://mcupdate.tumblr.com/");
	    }
	    // save properties to project root folder
	    prop.store(new FileOutputStream("config.properties"), null);

	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return prop.getProperty(key);
    }

}
