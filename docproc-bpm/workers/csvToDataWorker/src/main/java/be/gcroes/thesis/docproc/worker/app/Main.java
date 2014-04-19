package be.gcroes.thesis.docproc.worker.app;

import be.gcroes.thesis.docproc.worker.CsvToDataWorker;

public class Main {

	public static void main(String[] args) throws Exception{
        CsvToDataWorker worker = new CsvToDataWorker();
        Thread t = new Thread(worker);
        t.start();
    }

}
