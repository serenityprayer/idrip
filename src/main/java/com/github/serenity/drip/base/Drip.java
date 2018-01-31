package com.github.serenity.drip.base;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Drip implements Serializable {

	private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

	private static final long serialVersionUID = 4216987551972106945L;

	private long timestamp;

	private long workerId;

	private long datacenterId;

	private long sequence;

	private long geneId = 0;

	public Drip() {
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getWorkerId() {
		return workerId;
	}

	public void setWorkerId(long workerId) {
		this.workerId = workerId;
	}

	public long getDatacenterId() {
		return datacenterId;
	}

	public void setDatacenterId(long datacenterId) {
		this.datacenterId = datacenterId;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public long getGeneId() {
		return geneId;
	}

	public void setGeneId(long geneId) {
		this.geneId = geneId;
	}

	public String getFmtDateTime() {
		return sdf.format(timestamp);
	}

	@Override
	public String toString() {
		return "Drip{" +
				"timestamp=" + timestamp +
				", workerId=" + workerId +
				", datacenterId=" + datacenterId +
				", sequence=" + sequence +
				", geneId=" + geneId +
				", fmtDateTime='" + getFmtDateTime() + '\'' +
				'}';
	}
}
