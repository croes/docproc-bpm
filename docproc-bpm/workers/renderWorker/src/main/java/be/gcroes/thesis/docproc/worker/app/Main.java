package be.gcroes.thesis.docproc.worker.app;

import java.io.IOException;

import be.gcroes.thesis.docproc.worker.XslFoRenderWorker;

public class Main {

	public static void main(String[] args) throws IOException {
		XslFoRenderWorker worker = new XslFoRenderWorker();
		Thread t = new Thread(worker);
		t.start();
	}

}
