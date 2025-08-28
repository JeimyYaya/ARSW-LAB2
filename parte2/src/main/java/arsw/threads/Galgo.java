package arsw.threads;

/**
 * Un galgo que puede correr en un carril
 * 
 * @author rlopez
 * 
 */
public class Galgo extends Thread {
	private int paso;
	private Carril carril;
	RegistroLlegada regl;
	private static final Object lock = new Object();
	private static boolean paused = false;

	public Galgo(Carril carril, String name, RegistroLlegada reg) {
		super(name);
		this.carril = carril;
		paso = 0;
		this.regl=reg;
	}

	public void corra() throws InterruptedException {
		while (paso < carril.size()) {
			synchronized (lock) {
				while (paused) {
					lock.wait();
				}
			}
			Thread.sleep(100);
			carril.setPasoOn(paso++);
			carril.displayPasos(paso);
			
			if (paso == carril.size()) {						
				carril.finish();
				int ubicacion=regl.registraLlegada(this.getName()); 
				System.out.println("El galgo "+this.getName()+" llego en la posicion "+ubicacion);
			}
		}
	}


	@Override
	public void run() {
		
		try {
			corra();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void pauseAll(){
		synchronized (lock) {
			paused = true;
		}
	}

	public static void continueAll(){
		synchronized (lock) {
			paused = false;
			lock.notifyAll();
		}

	}

}
