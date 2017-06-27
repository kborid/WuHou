package com.prj.sdk.zip;

public interface ZipExtractorCallback {
	
	public void unZip(String inPath, String outPath, int status);
}
