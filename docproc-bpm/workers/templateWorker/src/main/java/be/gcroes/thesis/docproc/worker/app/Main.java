package be.gcroes.thesis.docproc.worker.app;

import java.io.IOException;

import be.gcroes.thesis.docproc.worker.TemplateToXslWorker;

public class Main {

	public static void main(String[] args) throws IOException {
		TemplateToXslWorker worker = new TemplateToXslWorker();
		Thread t = new Thread(worker);
		t.start();
	}

}
