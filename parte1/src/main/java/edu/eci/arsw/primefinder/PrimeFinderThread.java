package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;

public class PrimeFinderThread extends Thread{

	
	int a,b;
	boolean paused;
	
	private List<Integer> primes=new LinkedList<Integer>();
	
	public PrimeFinderThread(int a, int b) {
		super();
		this.a = a;
		this.b = b;
		this.paused = false;
	}

	public void run(){

		for (int i=a;i<=b;i++){	
			synchronized (this) {
				while (paused) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}				
			if (isPrime(i)){
				primes.add(i);
				System.out.println(i);
			}
		}
	}


	public void pauseThread(){
		paused = true;

	}

	public void resumeThread() {
        paused = false;
        synchronized (this) {
            notify();
        }
    }
	
	boolean isPrime(int n) {
	    if (n%2==0) return false;
	    for(int i=3;i*i<=n;i+=2) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
	
	
	

}
