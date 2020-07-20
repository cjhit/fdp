package com.github.cjhit.fdp.common.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String MD5(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			String pwd = new BigInteger(1, md.digest()).toString(16);
			int l = 32 - pwd.length();
			for (int i = l; i > 0; i--) {
				pwd = 0 + pwd;
			}
			return pwd;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Md5加密
	 * 
	 * @param password
	 * @return
	 */
	public static String encodeStr(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			StringBuilder sb = new StringBuilder();
			byte[] newByte = md.digest();
			// 转换�?6进制
			for (int i = 0; i < newByte.length; i++) {
				if ((newByte[i] & 0xff) < 0x10) {
					sb.append("0");
				}
				sb.append(Long.toString(newByte[i] & 0xff, 16));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getHash(String fileName, String hashType) throws Exception {
		InputStream fis;
		fis = new FileInputStream(fileName);
		byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);

		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	public static void readFile(String fileName) throws Exception {
		InputStream fis;
		fis = new FileInputStream(fileName);
		byte[] buffer = new byte[1024];
		while ((fis.read(buffer)) > 0) {

		}
		fis.close();
		return;
	}

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static void main(String args[]) throws NoSuchAlgorithmException {
		// a = e10adc3949ba59abbe56e057f20f883e
		String md5 = MD5Util.MD5("111111");
		System.out.println(md5);
	}
}
