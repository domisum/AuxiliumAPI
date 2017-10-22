package de.domisum.lib.auxilium.util;

import de.domisum.lib.auxilium.util.java.annotations.APIUsage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@APIUsage
public class FileUtil
{

	// READ
	@APIUsage public static String readFileToString(File file)
	{
		byte[] contentBytes = readFileToByteArray(file);
		return new String(contentBytes);
	}

	@APIUsage public static String readInputStreamToString(InputStream inputStream)
	{
		StringBuilder sb = new StringBuilder();

		try(BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF8")))
		{
			String line = br.readLine();
			while(line != null)
			{
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}

			return sb.toString();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@APIUsage public static byte[] readFileToByteArray(File file)
	{
		try
		{
			return Files.readAllBytes(file.toPath());
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}


	// WRITE
	@APIUsage public static void writeStringToFile(String path, String content)
	{
		writeStringToFile(new File(path), content);
	}

	@APIUsage public static void writeStringToFile(File file, String content)
	{
		try
		{
			createParentDirectory(file);
			try(FileWriter fw = new FileWriter(file))
			{
				fw.write(content);
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@APIUsage public static void writeByteArrayToFile(File file, byte[] data)
	{
		try
		{
			Files.write(file.toPath(), data);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	// COPY
	@APIUsage public static void copyFile(File origin, File destinationDir)
	{
		copyFile(origin, destinationDir, origin.getName());
	}

	@APIUsage public static void copyFile(File origin, File destinationDir, String newFileName)
	{
		if(!origin.exists())
			return;

		File destination = new File(destinationDir, newFileName);
		try
		{
			createDirectory(destinationDir);
			Files.copy(origin.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@APIUsage public static void copyDirectory(File originFolder, File destinationDir)
	{
		copyDirectory(originFolder, destinationDir, null);
	}

	@APIUsage public static void copyDirectory(File originFolder, File destinationDir, FilePathFilter filePathFilter)
	{
		try
		{
			createDirectory(destinationDir);

			for(File file : listFiles(originFolder))
			{
				if(filePathFilter != null && filePathFilter.isFilteredOut(file))
					continue;

				if(file.isFile())
					copyFile(file, destinationDir);
				else if(file.isDirectory())
				{
					File deeperDestination = new File(destinationDir, file.getName());
					createDirectory(deeperDestination);

					copyDirectory(file, deeperDestination, filePathFilter);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	@APIUsage public static void copyDirectory(File originFolder, String newName, File destinationDir)
	{
		try
		{
			createDirectory(destinationDir);

			for(File file : listFiles(originFolder))
			{
				if(file.isFile())
					copyFile(file, destinationDir);
				else if(file.isDirectory())
				{
					File deeperDestination = new File(destinationDir, newName != null ? newName : file.getName());
					createDirectory(deeperDestination);

					copyDirectory(file, null, deeperDestination);
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	// DELETE
	@APIUsage public static void deleteDirectory(File dir)
	{
		deleteDirectoryContents(dir);
		deleteFile(dir);
	}

	@APIUsage public static void deleteDirectoryContents(File dir)
	{
		if(dir == null)
			throw new IllegalArgumentException("The directory can't be null");

		for(File f : listFiles(dir))
		{
			if(f.isFile())
				deleteFile(f);
			else
				deleteDirectory(f);
		}
	}

	private static void deleteFile(File file)
	{
		try
		{
			boolean success = file.delete();
			if(!success)
				throw new IOException("Deleting file/dir '"+file+"' failed");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}


	// DIRECTORY
	private static void createParentDirectory(File file) throws IOException
	{
		createDirectory(file.getAbsoluteFile().getParentFile());
	}

	private static void createDirectory(File dir) throws IOException
	{
		if(dir.isDirectory())
			return;

		boolean success = dir.mkdirs();
		if(!success)
			throw new IOException("Failed to create directory directory '"+dir+"'");
	}


	// MISC
	@APIUsage public static boolean doesFileExist(String path)
	{
		File file = new File(path);
		return file.exists();
	}

	@APIUsage public static boolean isDirectoryEmpty(File dir)
	{
		return listFilesRecursively(dir).size() == 0;
	}


	@APIUsage public static File[] listFiles(File dir)
	{
		if(!dir.isDirectory())
			return new File[0];

		return dir.listFiles();
	}


	@APIUsage public static List<File> listFilesRecursively(File dir)
	{
		return listFilesRecursively(dir, true);
	}

	@APIUsage public static List<File> listFilesRecursively(File dir, boolean includeDirs)
	{
		List<File> files = new ArrayList<>();

		File[] filesArray = dir.listFiles();
		if(filesArray == null)
			throw new IllegalArgumentException("The directory '"+dir.getPath()+"' does not exist");

		for(File file : filesArray)
		{
			if(file.isDirectory())
			{
				files.addAll(listFilesRecursively(file, includeDirs));

				if(!includeDirs)
					continue;
			}

			files.add(file);
		}

		return files;
	}


	// SPECIFIC
	@APIUsage public static String getIdentifier(File baseDir, File file, String fileExtension)
	{
		String id = file.getPath().replaceFirst(baseDir.getPath(), "");
		id = id.replace('\\', '/');
		if(id.startsWith("/"))
			id = id.substring(1, id.length());

		id = TextUtil.replaceLast(id, fileExtension, "");

		return id;
	}


	// FILE FILTER
	@APIUsage
	public static class FilePathFilter
	{

		private Set<String> startsWithFilters = new HashSet<>();
		private Set<String> containsFilters = new HashSet<>();
		private Set<String> endsWithFilters = new HashSet<>();


		// GETTERS
		protected boolean isFilteredOut(File file)
		{
			for(String filter : this.startsWithFilters)
				if(file.getAbsolutePath().startsWith(filter))
					return true;

			for(String filter : this.containsFilters)
				if(file.getAbsolutePath().contains(filter))
					return true;

			for(String filter : this.endsWithFilters)
				if(file.getAbsolutePath().endsWith(filter))
					return true;

			return false;
		}


		// CHANGERS
		@APIUsage public FilePathFilter addStartsWith(String filter)
		{
			this.startsWithFilters.add(filter);
			return this;
		}

		@APIUsage public FilePathFilter addContains(String filter)
		{
			this.containsFilters.add(filter);
			return this;
		}

		@APIUsage public FilePathFilter addEndsWith(String filter)
		{
			this.endsWithFilters.add(filter);
			return this;
		}

	}

}
