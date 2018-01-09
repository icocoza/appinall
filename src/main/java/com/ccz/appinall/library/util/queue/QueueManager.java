package com.ccz.appinall.library.util.queue;

import java.util.ArrayList;
import java.util.List;

public abstract class QueueManager {
	private final int DefaultThreadCount = 16;
	protected Thread[] ququeThreads;
	protected List<QWorker> qWorkerList = new ArrayList<>();
	
	public QueueManager(int threadCount) {
		if(threadCount < 1)
			threadCount = DefaultThreadCount;
		ququeThreads = new Thread[threadCount];
	}
	
	public void addWorker(QWorker worker) {
		qWorkerList.add(worker);
	}
	
	public void start() {
		int i=0;
		for(Thread thread : ququeThreads) {
			QRunner qr = new QRunner(createQueueReader());
			for(QWorker worker : qWorkerList)
				qr.addWorker(worker);
			
			thread = new Thread(qr);
			thread.start();
		}
	}
	
	public void stop() {
		try{
			for(Thread t : ququeThreads) {
				t.interrupt();				
			}
		}catch(Exception e) {
		}
	}
	
	public abstract QReader createQueueReader();
}
