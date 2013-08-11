package com.lq.llmediaPlayer.Interface;

import com.lq.llmediaPlayer.Beans.DomElement;
/**
 * An <code>ItemFactory</code> can be used to instantiate a value object - such as Artist, Album, Track, Tag - from an XML element. Use the
 * {@link ItemFactoryBuilder} to obtain item factories for a specific type.
 *
 * @see com.andrew.apolloMod.helpers.lastfm.ItemFactoryBuilder
 * @see ResponseBuilder
 */
public interface ItemFactory<T> {
	
	/**
	 * Create a new instance of the type <code>T</code>, based on the passed {@link DomElement}.
	 *
	 * @param element the XML element
	 * @return a new object
	 */
	public T createItemFromElement(DomElement element);
}
