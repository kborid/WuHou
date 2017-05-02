
package com.prj.sdk.net.cache;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * LRU CACHE
 * @author yue
 *
 * @param <K>
 * @param <V>
 */
public class LruCache<K, V> extends LinkedHashMap<K ,V>{

	private static final long serialVersionUID = 1L;
	private int iMaxCacheByteSize	= 0;
	private int iMaxCacheItem		= 0;
	private int iCurrentSize 		= 0;

	/**
	 * if you want to handle your cache below the limitation size (ex: cache image),
	 * please use CacheType.SIZE, 
	 * otherwise use CacheType.NUMBER if you just want to handle how much objects in the cache,
	 */
	public enum CacheType{
		/**
		 * if you hope the cache size below the limitation, please use this attribute
		 */
		SIZE,

		/**
		 * if you hope the number of cached item below the limitation, please use this attribute
		 */
		NUMBER
	}
	private CacheType cacheType; 

	/**
	 * 
	 * @param iSize cache size
	 * @param type if you want to handle your cache below the limitation size (ex: cache image),
	 * please use CacheType.SIZE, otherwise use CacheType.NUMBER if you just want to handle how much objects in the cache,
	 * @throws IllegalArgumentException if the iSize is less than 0.
	 */
	public LruCache(int iSize, CacheType type) throws IllegalArgumentException{
		if (iSize<=0){
			throw new IllegalArgumentException("iSize 不能小于0");
		}

		cacheType = type;

		// if you want to cache image, we need to calculate the total size of all the cahced image.
		if (cacheType.equals(CacheType.SIZE)){
			this.iMaxCacheByteSize = iSize;
		}else{
			this.iMaxCacheItem = iSize;
		}
	}



	@Override
	public synchronized V put(K key, V value) {
		try {
			if (key==null){
				throw new NullPointerException("key 不能为null");
			}
			
			if(value == null) {
				return null;
			}
			
			V original = get(key);
			if(value.equals(original)) {
				return value;
			}
			
			if(this.containsKey(key)) {
				if (CacheType.SIZE.equals(cacheType)){				
	                this.iCurrentSize -= calculateSize(original);
				}else{
					this.iCurrentSize --;
				}
			}

			if (CacheType.SIZE.equals(cacheType)){				
                this.iCurrentSize += calculateSize(value);
			}else{
				this.iCurrentSize ++;
			}
			return super.put(key, value);	
		} catch (Exception e) {		
		}
	    return null;
	}
	@Override
	public synchronized V get(Object key) {
		try {
			return super.get(key);
		} catch (Exception e) {			
		}
		return null;
	}

	@Override
	public synchronized V remove(Object key) {
		try {
//			V mValue = get(key);
			
			return super.remove(key);
		} catch (Exception e) {			
		}
		return null;		
	}

	@Override
	public int size() {
		return iCurrentSize;
	}

	private int fnGetMaxSize(){
		return (CacheType.SIZE.equals(cacheType)) ? iMaxCacheByteSize:iMaxCacheItem;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		if (fnGetMaxSize() < size()){
			V mValue = eldest.getValue();
			if (CacheType.SIZE.equals(cacheType)){
				this.iCurrentSize = this.iCurrentSize - calculateSize(mValue);						
			}else{
				this.iCurrentSize --;
			}
			
			return true;
		}
		return false;
	}
	
	private  int calculateSize(Object object) {
		int size = 1;		
		if (object instanceof String) {
			size = sizeOfString((String) object);
		} else if (object instanceof Long) {
			size = 4 + 8;
		} else if (object instanceof Integer) {
			size = 4 + 4;
		} else if (object instanceof Boolean) {
			size = 4 + 1;
		} else if (object instanceof long[]) {
			long[] array = (long[]) object;
			size = 4 + array.length * 8;
		} else if (object instanceof byte[]) {
			byte[] array = (byte[]) object;
			size = 4 + array.length;
		} else if (object instanceof Serializable) {
			try {
				// Default to serializing the object out to determine size.
				NullOutputStream out = new NullOutputStream();
				ObjectOutputStream outObj = new ObjectOutputStream(out);
				outObj.writeObject(object);
				size = out.size();
			} catch (IOException ioe) {
			}
		} else if (object instanceof BitmapDrawable) {
			final Bitmap b = ((BitmapDrawable) object).getBitmap();
			if (b != null) {
				size = b.getRowBytes() * b.getHeight();
			}
		} else if (object instanceof Bitmap) {
			final Bitmap b = (Bitmap) object;
			if (b != null) {
				size = b.getRowBytes() * b.getHeight();
			}
		} else {
			throw new ClassCastException("非法缓存类型");
		}
		
		return size;
	}

	private int sizeOfString(String string) {
		if (string == null) {
			return 0;
		}
		return 4 + string.getBytes().length;
	}
	
	private static class NullOutputStream extends OutputStream {

		int	size	= 0;

		@Override
		public void write(int b) throws IOException {
			size++;
		}

		@Override
		public void write(byte[] b) throws IOException {
			size += b.length;
		}

		@Override
		public void write(byte[] b, int off, int len) {
			size += len;
		}

		/**
		 * Returns the number of bytes written out through the stream.
		 * 
		 * @return the number of bytes written to the stream.
		 */
		public int size() {
			return size;
		}
	}
}