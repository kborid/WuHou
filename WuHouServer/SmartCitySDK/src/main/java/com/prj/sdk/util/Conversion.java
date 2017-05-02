/*
 * Title                 :  Conversion.java
 * Package               :  org.gridbus.broker.alchemi
 * Project               :  Gridbus Broker
 * Description   :
 * Created on    :
 * Author                :
 * Copyright             :  (c) 2003, Grid Computing and Distributed Systems Laboratory,
 *                                          Dept. of Computer Science and Software Engineering,
 *                                          University of Melbourne, Australia.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later  version.
 * See the GNU General Public License (http://www.gnu.org/copyleft/gpl.html)for more details.
 *
 */

package com.prj.sdk.util;

//~--- JDK imports ------------------------------------------------------------

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * This class contains some methods for conversion of byte arrays, hex values etc.
 */
public class Conversion {

	/**
	 * Constants required to perform hex-encoding of a byte array.
	 */
	private static final char[]	hexArray	= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * A lookup table for the high order hex character.
	 */
	private static final byte[]	high		= new byte[16];

	/**
	 * A lookup table for the low order hex character.
	 */
	private static final byte[]	low			= new byte[16];

	static {
		byte b;

		for (int i = 0; i < low.length; i++) {
			b = (byte) i;
			low[i] = (byte) (b & 0x0f);
		}

		for (int i = 0; i < high.length; i++) {
			b = (byte) i;
			high[i] = (byte) ((b << 4) & 0xf0);
		}
	}

	/**
	 * Converts a byte[] array into a Hex string
	 * 
	 * @param inByteArray
	 * @return string
	 */
	public static String bytesToHexString(byte[] inByteArray) {
		return bytesToHexString(inByteArray, 0, inByteArray.length).toUpperCase();
	}

	/**
	 * Converts the given byte array into a hex string representation.
	 * 
	 * @param inByteArray
	 * @param offset
	 * @param len
	 * 
	 * @return a hex String representation of the byte array.
	 */
	public static String bytesToHexString(byte[] inByteArray, int offset, int len) {
		if (inByteArray == null) {
			return null;
		}

		int position;
		StringBuffer returnBuffer = new StringBuffer();

		for (position = offset; position < len; position++) {
			returnBuffer.append(hexArray[((inByteArray[position] >> 4) & 0x0f)]);
			returnBuffer.append(hexArray[(inByteArray[position] & 0x0f)]);
		}

		return returnBuffer.toString();
	}

	/**
	 * This function accepts a number of bytes and returns a long integer. Note that if the input is 8 bytes, and the high bit of the 0 byte is set, this will come out negative.
	 * 
	 * @param bytes
	 *            the byte array to convert to a long integer representation.
	 * @return a long integer representation of the byte array.
	 */
	public static long bytesToLong(byte[] bytes) {
		if (bytes.length > 8) {
			throw new IllegalArgumentException("Must specify 8 bytes or less");
		}

		long returnLong = 0;

		for (int n = 0; n < bytes.length; n++) {
			returnLong <<= 8;

			long aByte = (bytes[n] < 0) ? bytes[n] + 256 : bytes[n];

			returnLong = returnLong | aByte;
		}

		return returnLong;
	}

	/**
	 * This function accepts a number of bytes and returns an integer. Note that if the input is 4 bytes, and the high bit of the 0 byte is set, this will come out negative.
	 * 
	 * @param bytes
	 *            the byte array to convert to a long integer representation.
	 * @return a long integer representation of the byte array.
	 */
	public static int bytesToInt(byte[] bytes) {
		if (bytes.length > 4) {
			throw new IllegalArgumentException("Must specify 4 bytes or less");
		}

		int returnInt = 0;

		for (int n = 0; n < bytes.length; n++) {
			returnInt <<= 8;

			int aByte = (bytes[n] < 0) ? bytes[n] + 256 : bytes[n];

			returnInt = returnInt | aByte;
		}

		return returnInt;
	}

	public static int bytesToInt(byte[] bytes, int offset, int len) {
		if (bytes.length > 4) {
			throw new IllegalArgumentException("Must specify 4 bytes or less");
		}

		int returnInt = 0;

		for (int n = offset; n < bytes.length; n++) {
			returnInt <<= 8;

			int aByte = (bytes[n] < 0) ? bytes[n] + 256 : bytes[n];

			returnInt = returnInt | aByte;
		}

		return returnInt;
	}

	/**
	 * Returns the index for the given hex character in the byte array lookup array. This is used for both the hig order and low order hex characters.
	 * 
	 * @param c
	 *            the character to get the lookup index.
	 * @return an index into a byte array lookup table for the given hex character.
	 */
	private static int getIndex(char c) {
		if (('0' <= c) && (c <= '9')) {
			return ((byte) c - (byte) '0');
		} else if (('a' <= c) && (c <= 'f')) {
			return ((byte) c - (byte) 'a' + 10);
		} else if (('A' <= c) && (c <= 'F')) {
			return ((byte) c - (byte) 'A' + 10);
		} else {
			return -1;
		}
	}

	/**
	 * This method accepts a hex string and returns a byte array. The string must represent an integer number of bytes.
	 * 
	 * @param str
	 *            - the hex string to convert to byte array representation.
	 * @return the byte array representation of the hex string.
	 */
	public static byte[] hexStringToBytes(String str) {
		if (str == null || str.trim().length() == 0 || str.equalsIgnoreCase(""))
			return null;

		byte b;
		byte b2;
		int len = str.length();
		byte[] retval = new byte[len / 2];
		int j = 0;

		for (int i = 0; i < len; i += 2) {
			b = high[getIndex(str.charAt(i))];
			b2 = low[getIndex(str.charAt(i + 1))];
			retval[j++] = (byte) (b | b2);
		}

		return retval;
	}

	/**
	 * Converts a integer to a byte array representation.
	 * 
	 * @param i
	 *            the integer to convert to byte array representation.
	 * @return the byte array representation of the integer.
	 */
	public static byte[] intToBytes(int i) {
		int ii = i;
		byte[] returnBytes = new byte[8];

		for (int n = 3; n >= 0; n--) {
			returnBytes[n] = (byte) ii;
			ii = ii >>> 8;
		}

		return returnBytes;
	}

	/**
	 * Converts a long integer to a byte array representation. If the target array is shorter than needed, the hi bytes of the integer will be truncated.
	 * 
	 * @param l
	 *            the long integer to convert to byte array representation.
	 * @param len
	 *            the size of the byte array to return
	 * @return the byte array representation of the long integer.
	 */
	public static byte[] longToBytes(long l, int len) {
		byte[] returnValue = new byte[len];

		longToBytes(l, returnValue, 0, len);

		return returnValue;
	}

	/**
	 * Converts a long integer to a byte array representation. If the target array is shorter than needed, the hi bytes of the integer will be truncated.
	 * 
	 * @param l
	 *            the long integer to convert to byte array representation.
	 * @param b
	 * @param offset
	 * @param len
	 *            the size of the byte array to return
	 */
	public static void longToBytes(long l, byte[] b, int offset, int len) {
		int startPoint = (len < 8) ? len - 1 : 7;

		if (startPoint < 7) {
			l = l & (0xFFFFFFFFFFFFFFFFL >> (8 * (7 - startPoint)));
		}

		for (int n = len - 1; n >= 0; n--) {
			b[offset + n] = (byte) l;
			l = l >> 8;
		}
	}

	/**
	 * Converts a byte in range -128 <= i <= 127 to an int in range 0 <= i <= 255.
	 * 
	 * @param i
	 *            the byte to convert
	 * @return the converted integer
	 */
	public static int unsignedByteToInt(byte i) {
		return (i < 0) ? (i + 256) : i;
	}

	/**
	 * Use if you don't want to have to catch a million UnsupportedEncodingExceptions.
	 * 
	 * @param in
	 * @param encoding
	 * @return byte[]
	 */
	public static byte[] stringToByteArray(String in, String encoding) {
		try {
			return in.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Character encoding not supported: " + encoding);
		}
	}

	/**
	 * Use if you don't want to have to catch a million UnsupportedEncodingExceptions.
	 * 
	 * @param in
	 * @param encoding
	 * @return String
	 */
	public static String byteArrayToString(byte[] in, String encoding) {
		try {
			return new String(in, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Character encoding not supported: " + encoding);
		}
	}

	/**
	 * Converts a BigInteger to a byte array.
	 * 
	 * @return the BigInteger converted to a byte array
	 * @param b
	 *            the java.math.BigInteger to convert to a byte array
	 */
	public static byte[] bigIntegerToUnsigedBytes(BigInteger b) {

		// Convert the BigInteger to a byte array
		byte[] bytes = b.toByteArray();

		if (b.signum() == -1) {
			throw new IllegalArgumentException("Only taking positive BigIntegers");
		}

		while (bytes[0] == 0x00) {
			byte[] bytes2 = new byte[bytes.length - 1];

			System.arraycopy(bytes, 1, bytes2, 0, bytes2.length);
			bytes = bytes2;
		}

		return bytes;
	}
}

// ~ Formatted by Jindent --- http://www.jindent.com
