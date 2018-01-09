package com.ccz.appinall.library.util.queue;

import java.util.HashMap;
import java.util.Map;

class QRunner implements Runnable {

	private QReader qReader;
	private Map<Integer, QWorker> qWorkerList = new HashMap<>();
	
	private int nonDataSleepMillitime = 200;
	
	public QRunner(QReader qReader) {
		this.qReader = qReader;
	}
	
	public QRunner(QReader qReader, int nonDataSleepMillitime) {
		this.qReader = qReader;
		this.nonDataSleepMillitime = nonDataSleepMillitime;
	}
	
	public void addWorker(QWorker worker) {
		qWorkerList.put(worker.cmd, worker);
	}
	
	@Override
	public void run() {
		while(Thread.currentThread().isInterrupted()==false) {
			try{
				if(qReader.pop()==false) {
					Thread.sleep(nonDataSleepMillitime);
					continue;
				}
				QWorker worker = qWorkerList.get(qReader.cmd);
				if(worker == null)
					continue;
				worker.doWork(qReader.data);
				continue;
			}catch(Exception e) {
			}
			try {
				Thread.sleep(nonDataSleepMillitime);
			} catch (InterruptedException e) {
			}
		}
	}

}
