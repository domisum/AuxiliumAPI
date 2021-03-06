package io.domisum.lib.auxiliumlib.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtilTest
{
	
	private final List<File> filesToTearDown = new ArrayList<>();
	
	
	@AfterEach
	public void tearDown()
	{
		for(var file : filesToTearDown)
			if(file != null)
				if(file.isFile())
					FileUtil.deleteFile(file);
				else
					FileUtil.deleteDirectory(file);
	}
	
	
	// TEST: STRING
	@Test
	public void testSimpleReadAndWrite()
	{
		String text = "Hello world";
		var tempFile = createTempFile();
		
		writeReadAssertEquals(tempFile, text);
	}
	
	@Test
	public void testWriteInNotExistingFolder()
	{
		String text = "meme\nasdf";
		var tempDir = createTempDirectory();
		var deeperInTempDir = new File(tempDir, "folder1/folder2");
		
		writeReadAssertEquals(deeperInTempDir, text);
	}
	
	
	// TEST: RAW
	@Test
	public void testSimpleWriteReadRaw()
	{
		byte[] testData = {0, 8, -3, 127};
		var tempFile = createTempFile();
		
		writeReadAssertEquals(tempFile, testData);
	}
	
	@Test
	public void testOverwriteRaw()
	{
		byte[] testData = {-1, -29, 88, 18};
		var tempFile = createTempFile();
		
		FileUtil.writeRaw(tempFile, testData);
		byte[] testData2 = {0, 8, -3, 127};
		writeReadAssertEquals(tempFile, testData2);
	}
	
	
	// TEST: DIRECTORY
	@Test
	public void testDeleteNestedDir()
	{
		String sampleText = "asdf\nwow";
		
		var tempDir = createTempDirectory();
		
		var textFileInDir = new File(tempDir, "test.txt");
		FileUtil.writeString(textFileInDir, sampleText);
		var fileDeeper = new File(tempDir, "dir/test2.txt");
		FileUtil.writeString(fileDeeper, sampleText);
		
		FileUtil.deleteDirectory(tempDir);
		Assertions.assertFalse(tempDir.exists(), "directory wasn't deleted");
	}
	
	
	// TEST: TEMP FILES
	@Test
	public void testTempFilesExtensions()
	{
		var tempFile = FileUtil.createTemporaryFile("zip");
		filesToTearDown.add(tempFile);
		Assertions.assertTrue(tempFile.getPath().endsWith(".zip"), "wrong file extension :"+tempFile.getPath());
		
		var tempFile2 = FileUtil.createTemporaryFile(".bat");
		filesToTearDown.add(tempFile2);
		Assertions.assertTrue(tempFile2.getPath().endsWith(".bat"), "wrong file extension :"+tempFile2.getPath());
	}
	
	
	// TEST: GENERAL FILE
	@Test
	void testCompositeFileExtension()
	{
		Assertions.assertEquals("json", FileUtil.getCompositeExtension(new File("wow/test.json")));
		Assertions.assertEquals("config.json", FileUtil.getCompositeExtension(new File("test.config.json")));
		
		Assertions.assertEquals("json", FileUtil.getCompositeExtension(new File("wow.xd/test.json")));
		Assertions.assertEquals("config.json", FileUtil.getCompositeExtension(new File("wow.xd/test.config.json")));
	}
	
	@Test
	void testNameWithoutCompositeExtension()
	{
		Assertions.assertEquals("test", FileUtil.getNameWithoutCompositeExtension(new File("wow/test.json")));
		Assertions.assertEquals("test", FileUtil.getNameWithoutCompositeExtension(new File("wow/test.meme.json")));
		Assertions.assertEquals("archive", FileUtil.getNameWithoutCompositeExtension(new File("folder/archive.tar.gz")));
	}
	
	
	// ARRANGE
	private File createTempFile()
	{
		var tempFile = FileUtil.createTemporaryFile();
		filesToTearDown.add(tempFile);
		return tempFile;
	}
	
	private File createTempDirectory()
	{
		var tempDir = FileUtil.createTemporaryDirectory();
		filesToTearDown.add(tempDir);
		return tempDir;
	}
	
	
	// ACT ASSERT
	private void writeReadAssertEquals(File file, String text)
	{
		FileUtil.writeString(file, text);
		String read = FileUtil.readString(file);
		
		Assertions.assertEquals(text, read, "text was different after write/read");
	}
	
	private void writeReadAssertEquals(File file, byte[] raw)
	{
		FileUtil.writeRaw(file, raw);
		byte[] read = FileUtil.readRaw(file);
		
		Assertions.assertArrayEquals(raw, read, "raw data was different after write/read");
	}
	
}
