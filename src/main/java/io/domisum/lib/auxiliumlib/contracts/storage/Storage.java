package io.domisum.lib.auxiliumlib.contracts.storage;

import io.domisum.lib.auxiliumlib.contracts.source.optional.FiniteOptionalSource;
import io.domisum.lib.auxiliumlib.annotations.API;

/**
 * Interface describing the contract of a storage module. The purpose of the module is to store objects of the type
 * {@code T} and be able to retrieve the instances later on. This contract does not guarantee persistence,
 * which could cause loss of stored data on shutdown. To check if this is the case, check the actual implementation of the
 * Storage module.
 * <p>
 * A storage module implementing this interface can only hold one item per id.
 *
 * @param <T> the type of item to be stored
 */
public interface Storage<KeyT, T> extends FiniteOptionalSource<KeyT, T>
{

	/**
	 * Stores a {@code T} in this module in order to fetch it later on. If the storage module already contains
	 * another item with the same key, the old item is discarded and the new item stored in its place.
	 *
	 * @param item the item to store
	 */
	@API
	void store(T item);

	/**
	 * Removes the item with the supplied key from the storage module.
	 *
	 * @param key the key of the item to remove
	 */
	@API
	void remove(KeyT key);

}
