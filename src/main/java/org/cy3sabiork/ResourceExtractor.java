package org.cy3sabiork;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.BundleContext;



public class ResourceExtractor {
	private static File appDirectory;
	private final BundleContext bc;

	public ResourceExtractor(final BundleContext bc, final File appDirectory) {
		this.bc = bc;
		setAppDirectory(appDirectory);
	}
	
	public static void setAppDirectory(File appDirectory){
		ResourceExtractor.appDirectory = appDirectory;
	}
	
	/**
	 * Returns the file URI in the application folder
	 * for the given resource string.
	 * 
	 * For instance "/gui/query.html"
	 */
	public static String fileURIforResource(String resource){
		File file = new File(appDirectory + resource);
		URI fileURI = file.toURI();	    
		return fileURI.toString();
	}
	
	public void test(){		
		File ftest = bc.getDataFile("gui/info.html");
		System.out.println(ftest.getAbsolutePath());
		System.out.println("File exists: " + ftest.exists());
		
		
		// copying the file does not work
		// Files.copy(ftest.toPath(), new File(appDirectory + "/" + "info.html").toPath(), 
		//		StandardCopyOption.REPLACE_EXISTING);
		
		
		URL infoURL = getClass().getResource("/gui/info.html");
		System.out.println(infoURL);
		
		/*
		InputStream inputStream = infoURL.openStream();
		OutputStream outputStream = new FileOutputStream(new File(appDirectory + "/" + "info.html"));

		
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		*/
		
		
		/*
		Bundle bundle = bc.getBundle();
		@SuppressWarnings("unchecked")
		Enumeration<String> e = bundle.getEntryPaths("/gui/");
		while(e.hasMoreElements()){
			String path = e.nextElement();
			System.out.println(path);
			
			// skip directories
			if (path.endsWith("/")){
				continue;
			}
			File file = bc.getDataFile(path);
			System.out.println(file.getAbsolutePath());
			
			// copy to app folder
			Path src = file.toPath();	
			Path des = new File(appDirectory.toPath() + "/" + path).toPath();
			System.out.println(src + " -> " + des);
			Files.copy(file.toPath(), des, StandardCopyOption.REPLACE_EXISTING);	
		}
		*/
	}
	
	
	
	/*
	public final void extractResource(String location) throws IOException {

		// Get the location of web preview template
		final URL source = this.getClass().getClassLoader().getResource(location);
		final File destination = appDirectory;

		// Unzip resource to this directory in CytoscapeConfig
		if (!destination.exists() || !destination.isDirectory()) {
			unzipTemplate(source, destination);
		} else if(destination.exists()) {
			// Maybe there is an old version
			final File versionFile = new File(destination, VERSION_NAME);
			if(!versionFile.exists()) {
				logger.info("Version file not found.  Creating new preview template...");
				deleteAll(destination);
				unzipTemplate(source, destination);
			} else {
				// Check version number
				final String contents = Files.lines(Paths.get(versionFile.toURI()))
					.reduce((t, u) -> t+u).get();
				
				logger.info("Preview template version: " + contents);
				logger.info("Current template version: " + VERSION);
				
				if(!contents.equals(VERSION)) {
					logger.info("Updating template to version " + VERSION);
					deleteAll(destination);
					unzipTemplate(source, destination);
				} else {
					logger.info("No need to update preview template.");
				}
			}
		}
	}
	*/
	
	private final void deleteAll(final File f) {
		if(f.isDirectory()) {
			final File[] files = f.listFiles();
			Arrays.stream(files).forEach(file->deleteAll(file));
		}
		f.delete();
	}

	public void unzipTemplate(final URL source, final File destDir) throws IOException {

		destDir.mkdir();
		final ZipInputStream zipIn = new ZipInputStream(source.openStream());

		ZipEntry entry = zipIn.getNextEntry();
		while (entry != null) {
			final String filePath = destDir.getPath() + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				unzipEntry(zipIn, filePath);
			} else {
				final File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private final void unzipEntry(final ZipInputStream zis, final String filePath) throws IOException {
		final byte[] buffer = new byte[4096];
		final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		int read = 0;
		while ((read = zis.read(buffer)) != -1) {
			bos.write(buffer, 0, read);
		}
		bos.close();
	}
	
	
}
