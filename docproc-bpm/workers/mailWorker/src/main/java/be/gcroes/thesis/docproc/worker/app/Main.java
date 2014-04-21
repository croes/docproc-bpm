package be.gcroes.thesis.docproc.worker.app;

import java.io.IOException;

import be.gcroes.thesis.docproc.worker.MailWorker;

public class Main {

	public static void main(String[] args) throws IOException {
		MailWorker worker = new MailWorker();
		Thread t = new Thread(worker);
		t.start();
	}
}
