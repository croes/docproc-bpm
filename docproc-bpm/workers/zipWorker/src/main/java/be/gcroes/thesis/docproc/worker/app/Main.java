package be.gcroes.thesis.docproc.worker.app;

import java.io.IOException;

import be.gcroes.thesis.docproc.worker.ZipWorker;

public class Main {
	
	public static void main(String[] args) throws IOException {
		ZipWorker worker = new ZipWorker();
		Thread t = new Thread(worker);
		t.start();
	}

}
