package io.domisum.lib.auxiliumlib.contracts.storage;

import com.google.gson.JsonSyntaxException;
import io.domisum.lib.auxiliumlib.annotations.API;
import io.domisum.lib.auxiliumlib.contracts.Keyable;
import io.domisum.lib.auxiliumlib.contracts.serialization.ToStringSerializer;
import io.domisum.lib.auxiliumlib.util.file.FileUtil;
import io.domisum.lib.auxiliumlib.util.file.FileUtil.FileType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@API
@RequiredArgsConstructor
public class KeyableInDirectoryStorage<KeyT, T extends Keyable<KeyT>>
		implements Storage<KeyT,T>
{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	// SETTINGS
	private final File directory;
	private final String fileExtension;
	private final ToStringSerializer<T> serializer;
	
	
	// SOURCE
	@Override
	public Collection<T> fetchAll()
	{
		var files = FileUtil.listFilesRecursively(directory, FileType.FILE);
		var validFiles = new ArrayList<File>();
		for(var file : files)
			if(isFileExtensionValid(file))
				validFiles.add(file);
		return readFromFiles(validFiles);
	}
	
	private boolean isFileExtensionValid(File file)
	{
		String extension = FileUtil.getCompositeExtension(file);
		boolean isValid = Objects.equals(fileExtension, extension);
		if(!isValid)
			logger.warn("Storage directory contains file with invalid extension ({}), skipping: {}", extension, file.getName());
		return isValid;
	}
	
	@API
	protected Collection<T> readFromFiles(Collection<File> files)
	{
		var items = new ArrayList<T>();
		for(var file : files)
			items.add(readFromFile(file));
		return items;
	}
	
	
	@Override
	public boolean contains(KeyT key)
	{
		var file = getFileFor(key);
		return isFileValid(file);
	}
	
	@Override
	public Optional<T> fetch(KeyT key)
	{
		var file = getFileFor(key);
		if(!isFileValid(file))
			return Optional.empty();
		return Optional.of(readFromFile(file));
	}
	
	
	// STORAGE
	@Override
	public void store(T item)
	{
		String serialized = serializer.serialize(item);
		var file = getFileFor(item.getKey());
		FileUtil.writeString(file, serialized);
	}
	
	@Override
	public void remove(KeyT key)
	{
		var file = getFileFor(key);
		if(!isFileValid(file))
			return;
		FileUtil.deleteFile(file);
	}
	
	
	// FILE
	@API
	protected T readFromFile(File file)
	{
		String serialized = FileUtil.readString(file);
		T deserialized = deserialize(file, serialized);
		if(deserialized == null)
			throw new IllegalStateException("deserializer returned null for content of file: "+file);
		return deserialized;
	}
	
	private T deserialize(File file, String toDeserialize)
	{
		try
		{
			return serializer.deserialize(toDeserialize);
		}
		catch(RuntimeException e)
		{
			FileUtil.deleteFile(file);
			logger.error("Deleting file {}, because it contains invalid data", file);
			throw new JsonSyntaxException("Failed to deserialize content of file "+file, e);
		}
	}
	
	
	private boolean isFileValid(File file)
	{
		return file.exists() && file.isFile();
	}
	
	private File getFileFor(KeyT key)
	{
		String fileName = key.toString()+fileExtension;
		return new File(directory, fileName);
	}
	
}