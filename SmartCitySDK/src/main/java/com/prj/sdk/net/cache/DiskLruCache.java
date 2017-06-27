/*
 * Copyright 2013 Ken Yang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.prj.sdk.net.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

import android.graphics.Bitmap;
import android.os.StatFs;

import com.prj.sdk.util.PoolFIFOExecutor;
import com.prj.sdk.util.ThumbnailUtil;

/**
 * Least Recently Used(LRU):  Remove the least recently used items first.
 * @author Ken Yang
 *	
 * @param <K> Key
 * @param <V> Value
 */
public class DiskLruCache<K, V> extends LinkedHashMap<K ,V>{

	private static final long serialVersionUID = 1L;
	private int iMaxCacheByteSize	= 0;
	private int iMaxCacheItem		= 0;
	private int iCurrentSize 		= 0;

	/**
	 * if you want to handle your cache below the limitation size (ex: cache image),
	 * please use DiskCacheType.SIZE, 
	 * otherwise use DiskCacheType.NUMBER if you just want to handle how much objects in the cache,
	 */
	public enum DiskCacheType{
		/**
		 * if you hope the cache size below the limitation, please use this attribute
		 */
		SIZE,

		/**
		 * if you hope the number of cached item below the limitation, please use this attribute
		 */
		NUMBER
	}
	
	private File mCacheBase;
	private DiskCacheType DiskCacheType; 

	/**
	 * 
	 * @param iSize cache size
	 * @param type if you want to handle your cache below the limitation size (ex: cache image),
	 * please use DiskCacheType.SIZE, otherwise use DiskCacheType.NUMBER if you just want to handle how much objects in the cache,
	 * @throws IllegalArgumentException if the iSize is less than 0.
	 */
	public DiskLruCache(File cacheBase, int iSize, DiskCacheType type) throws IllegalArgumentException{
		if (iSize<=0){
			throw new IllegalArgumentException("iSize 不能小于0");
		}
		
        if(!cacheBase.exists()) {
        	cacheBase.mkdirs();
        }
		
		mCacheBase = cacheBase;
		DiskCacheType = type;
        
	    final long freeSpace = getFreeSpace();
	    iSize = (int)Math.min(iSize, freeSpace / 10);
		 
		// if you want to cache image, we need to calculate the total size of all the cahced image.
		if (DiskCacheType.equals(DiskCacheType.SIZE)){
			this.iMaxCacheByteSize = iSize;
		}else{
			this.iMaxCacheItem = iSize;
		}
		
		PoolFIFOExecutor.exe(new Runnable() {
			
			@Override
			public void run() {
				initParams();				
			}
		});
	}
    
	protected void initParams() {		
			File[] files =  mCacheBase.listFiles(new FileFilter() {			
				@Override
				public boolean accept(File mFile) {				
					return mFile.isFile();
				}
			});
			Arrays.sort(files, new CompratorByLastModified());
			for (int i = 0, len = files.length; i < len; i++) {			
				try {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(files[i]));
				K key = (K)in.readObject();				
				in.close();				
								
				if (DiskCacheType.SIZE.equals(DiskCacheType)){				
	                this.iCurrentSize += files[i].length();
				}else{
					this.iCurrentSize ++;
				}
				
				super.put(key, null);
				} catch (Exception e) {
					files[i].delete();
					e.printStackTrace();
				}		
			}		
	}
	
	/**
	 * 文件比较
	 * 
	 * @author
	 */
	private static class CompratorByLastModified implements Comparator<File> {
		public int compare(File f1, File f2) {
			long diff = f1.lastModified() - f2.lastModified();
			if (diff > 0)
				return 1;
			else if (diff == 0)
				return 0;
			else
				return -1;
		};

		public boolean equals(Object obj) {
			return true;
		};

	}
	
	protected File getFile(K key) {
		return new File(mCacheBase, getMD5(key.toString()));
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
				File mFile = getFile(key);				
				if(this.containsKey(key) && value.equals(original)) {
					return value;
				} else if(this.containsKey(key) && !value.equals(original)) {
					if (DiskCacheType.SIZE.equals(DiskCacheType)){				
		                this.iCurrentSize -= mFile.length();
					}else{
						this.iCurrentSize --;
					}
				}
				
				ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(mFile));
				out.writeObject(key);
				out.writeObject(value.getClass().getName());
				if(Bitmap.class.getName().equals(value.getClass().getName())) {
					out.writeObject(bitmap2Bytes((Bitmap)value));
				} else {
					out.writeObject(value);
				}				
				out.close();
				
				if (DiskCacheType.SIZE.equals(DiskCacheType)){				
	                this.iCurrentSize += mFile.length();
				}else{
					this.iCurrentSize ++;
				}					
				super.put(key, null);			
		} catch (Exception e) {
			e.printStackTrace();
			if(key != null) {
				File mFile = getFile((K)key);
				mFile.delete();
			}			
			return null;
		}
		return value;
	}
	
	@Override
	public synchronized V get(Object key) {
		V value = null;
		try {
			if (key==null){
				throw new NullPointerException("key 不能为null");
			}
			
			File mFile = getFile((K)key);			
			if(!mFile.exists()) {				
				return null;
			}
			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(mFile));
			K temp = (K)in.readObject();
			String mClass = (String)in.readObject();
			if(Bitmap.class.getName().equals(mClass)) {
				byte[] data = (byte[])in.readObject();
				value = (V)ThumbnailUtil.getImageThumbnail(data, 0, 0, false);					
			} else {
				value = (V)in.readObject();
			}			
			in.close();			
			super.get(key);
		} catch (Exception e) {		
            e.printStackTrace();
            if(key != null) {
				File mFile = getFile((K)key);
				mFile.delete();
			}			
		}
		return value;
	}


	@Override
	public synchronized V remove(Object key) {
		V value = null;
		try {
			value = get(key);			
			File mFile = getFile((K)key);
			mFile.delete();
			super.remove(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	private byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	@Override
	public int size() {
		return iCurrentSize;
	}

	private int fnGetMaxSize(){
		return (DiskCacheType.SIZE.equals(DiskCacheType)) ? iMaxCacheByteSize:iMaxCacheItem;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		if (fnGetMaxSize() < size()){
			File mFile = getFile(eldest.getKey());
			if (DiskCacheType.SIZE.equals(DiskCacheType)){
				this.iCurrentSize = this.iCurrentSize - (int)mFile.length();						
			}else{
				this.iCurrentSize --;
			}
			mFile.delete();					
			return true;
		}
		return false;
	}	
	
	private long getFreeSpace() {
		// maybe make singleton
		final StatFs stat = new StatFs(mCacheBase.getAbsolutePath());
		return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
	}
	 
	public static String getMD5(String key) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(key.getBytes("UTF-8"));

			byte b[] = messageDigest.digest();

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				int i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			return buf.toString().toLowerCase();
		} catch (Exception e) {

		}

		return "";
	}

}