package edu.eci.arsw.primefinder;

public class Main {

	public static void main(String[] args) {
		int max = 500000000;
        int part = max / 3;

        PrimeFinderThread pft1 = new PrimeFinderThread(0, part);
        PrimeFinderThread pft2 = new PrimeFinderThread(part + 1, 2 * part);
        PrimeFinderThread pft3 = new PrimeFinderThread(2 * part + 1, max);

        pft1.start();
        pft2.start();
        pft3.start();
    }
	
}
