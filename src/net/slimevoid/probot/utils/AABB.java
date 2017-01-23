package net.slimevoid.probot.utils;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AABB {

	public final float minX, maxX, minY, maxY;
	
	public AABB(float minX, float maxX, float minY, float maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}
	
	public AABB(double minX, double maxX, double minY, double maxY) {
		this((float)minX, (float)maxX, (float)minY, (float)maxY);
	}

	public AABB union(AABB other) {
		return new AABB(min(this.minX, other.minX),
						max(this.maxX, other.maxX),
						min(this.minY, other.minY),
						max(this.maxY, other.maxY));
	}
}
