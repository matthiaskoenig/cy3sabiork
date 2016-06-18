package org.cy3sabiork;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * Class extracts the bundle resources to given app directory.
 * This provides access to the local resources via 
 * file:// uris. 
 * Required to provide JavaFX access to bundle resources, which
 * are currently not supported. 
 */
public class ResourceExtractor {
	/* Resources made available via the ResourceExtractor */
	public final String GUI_RESOURCES = "/gui/";   
	
	private static File appDirectory;
	private final BundleContext bc;

	public ResourceExtractor(final BundleContext bc, final File appDirectory) {
		this.bc = bc;
		setAppDirectory(appDirectory);
	}
	
	public static void setAppDirectory(File appDirectory){
		ResourceExtractor.appDirectory = appDirectory;
	}
	
	/* 
	 * Replacement of
	 * 	getClass().getResource("/gui/info.html");
	 * which does not work for bundle resources in context of JavaFX. 
	 */
	public static String getResource(String resource){
		return fileURIforResource(resource);
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
	
	/** 
	 * Extracts the bundle resources from the BundleContext in the 
	 * application directory.
	 * 
	 * BundleContext and application directory have to be provided.
	 */
	public void extract(){
		if (bc == null || appDirectory == null){
			System.out.println("WARNING BundleContext or application directory not set. " +
					"Files not extracted");
			return;
		}
		
		Bundle bundle = bc.getBundle();
		@SuppressWarnings("unchecked")
		// bundle root
		URL entry = bundle.getEntry("/");
		System.out.println("bundle root: " + entry);
		
		// clean old resources
		
		
		
		// list all GUI resources of bundle and extract them
		Enumeration<String> e = bundle.getEntryPaths(GUI_RESOURCES);
		while(e.hasMoreElements()){
			String path = e.nextElement();
			// System.out.println(path);
			
			// skip directories
			/*
			if (path.endsWith("/")){
				continue;
			}
			*/
				
			// copy via stream from bundle URL to application file
			try {
				URL inURL = new URL(entry.toString() + path);

				try {
					InputStream inputStream = inURL.openConnection().getInputStream();
					
					File outFile = new File(appDirectory + "/" + path);
					// create directories if necessary
					File parent = outFile.getParentFile();
					if (!parent.exists() && !parent.mkdirs()){
					    throw new IllegalStateException("Couldn't create dir: " + parent);
					}
					
					System.out.println(" --> " + outFile.getAbsolutePath());
					OutputStream outputStream = new FileOutputStream(outFile);
			
					int read = 0;
					byte[] bytes = new byte[1024];
			
					while ((read = inputStream.read(bytes)) != -1) {
							outputStream.write(bytes, 0, read);
					}
					outputStream.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();
					return;
				}
				
				
			} catch (MalformedURLException urlException) {
				urlException.printStackTrace();
				return;
			}

			
			/*
			 // Delete if resources are already available
			 if(destination.exists()) {
					// Maybe there is an old version
					final File versionFile = new File(destination, VERSION_NAME);
					if(!versionFile.exists()) {
						logger.info("Version file not found.  Creating new preview template...");
						deleteAll(destination);
			*/
						
		}
		
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

	/* Handling zip resources. */
	private void unzipTemplate(final URL source, final File destDir) throws IOException {

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
