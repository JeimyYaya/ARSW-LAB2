package edu.eci.arsw.primefinder;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int max = 500000000;
        int part = max / 3;

        PrimeFinderThread pft1 = new PrimeFinderThread(0, part);
        PrimeFinderThread pft2 = new PrimeFinderThread(part + 1, 2 * part);
        PrimeFinderThread pft3 = new PrimeFinderThread(2 * part + 1, max);

        pft1.start();
        pft2.start();
        pft3.start();

		Scanner scanner = new Scanner(System.in);
		
		while (pft1.isAlive() || pft2.isAlive() || pft3.isAlive()) {
			Thread.sleep(5000);
			pft1.pauseThread();
			pft2.pauseThread();
			pft3.pauseThread();

			String input = scanner.nextLine();

			if (input.isEmpty()){
				pft1.resumeThread();
				pft2.resumeThread();
				pft3.resumeThread();
			}
		}	

    }

}
