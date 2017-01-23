package net.slimevoid.probot.network.p2p;

import java.nio.ByteBuffer;

class TimeRequest {
	
	int reqID;
	int pingEst;
	long timeEst;
	long timeSent;
	
	long timeCorrect;
	long timeRcv;
	
	public TimeRequest(int reqID, int pingEst, long timeEst, long timeSent) {
		this.reqID = reqID;
		this.pingEst = pingEst;
		this.timeEst = timeEst;
		this.timeSent = timeSent;
		timeCorrect = -1;
		timeRcv = -1;
	}
	
	public void answer(long timeCorrect, long timeRcv) {
		this.timeCorrect = timeCorrect;
		this.timeRcv = timeRcv;
	}
	
	public boolean isAnswered() {
		return timeCorrect >= 0;
	}
	
	public void write(ByteBuffer data) {
		data.putInt(reqID);
		data.putInt(pingEst);
		data.putLong(timeEst);
		data.putLong(timeSent);
	}
	
	public static TimeRequest read(byte[] data) {
		ByteBuffer buf = ByteBuffer.allocate(data.length);
		buf.put(data);
		buf.position(1);
		return new TimeRequest(buf.getInt(), buf.getInt(), buf.getLong(), buf.getLong());
	}
}
