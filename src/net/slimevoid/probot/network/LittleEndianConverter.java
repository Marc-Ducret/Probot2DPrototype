package net.slimevoid.probot.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianConverter {
	public static int readLittleEndianInt(InputStream in) throws IOException {
        int ch4 = in.read();
        int ch3 = in.read();
        int ch2 = in.read();
        int ch1 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}
	
	public static float readLittleEndianFloat(InputStream in) throws IOException {
		return Float.intBitsToFloat(readLittleEndianInt(in));
	}
	
	public static long readLittleEndianLong(DataInputStream in) throws IOException {
		byte buffer[] = new byte[8];
		in.readFully(buffer, 0, 8);
        return  ((long)(buffer[7] & 0xFF) << 56) +
                ((long)(buffer[6] & 0xFF) << 48) +
                ((long)(buffer[5] & 0xFF) << 40) +
                ((long)(buffer[4] & 0xFF) << 32) +
                ((long)(buffer[3] & 0xFF) << 24) +
                ((long)(buffer[2] & 0xFF) << 16) +
                ((long)(buffer[1] & 0xFF) <<  8) +
                ((long)(buffer[0] & 0xFF) <<  0);
	}
	
	public static double readLittleEndianDouble(DataInputStream in) throws IOException {
		return Double.longBitsToDouble(readLittleEndianLong(in));
	}
	
	public static void writeLittleEndianLong(DataOutputStream out, long l) throws IOException {
		byte buffer[] = new byte[8];
		buffer[7] = (byte)(l >>> 56);
		buffer[6] = (byte)(l >>> 48);
		buffer[5] = (byte)(l >>> 40);
		buffer[4] = (byte)(l >>> 32);
		buffer[3] = (byte)(l >>> 24);
		buffer[2] = (byte)(l >>> 16);
        buffer[1] = (byte)(l >>>  8);
        buffer[0] = (byte)(l >>>  0);
        out.write(buffer, 0, 8);
	}
	
	public static void writeLittleEndianInt(DataOutputStream out, int i) throws IOException {
		out.write((i >>>  0) & 0xFF);
		out.write((i >>>  8) & 0xFF);
		out.write((i >>> 16) & 0xFF);
		out.write((i >>> 24) & 0xFF);
	}
}
