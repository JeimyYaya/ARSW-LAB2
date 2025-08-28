# Escuela Colombiana de Ingeniería

**Arquitecturas de Software – ARSW**
-  Jeimy Yaya


## 📌 Parte I 

### 🧵 Creación, puesta en marcha y coordinación de hilos.

#### 1️⃣ Revisión  el programa “primos concurrentes”    
Este es un programa que calcula los números primos entre dos intervalos, distribuyendo la búsqueda de los mismos entre hilos independientes. Por ahora, tiene un único hilo de ejecución que busca los primos entre 0 y 30.000.000. Ejecútelo, abra el administrador de procesos del sistema operativo, y verifique cuantos núcleos son usados por el mismo.   

✔️ Se evidencia el uso de **4 núcleos**.  
(Cuando uno de los núcleos baja, otro lo sustituye, por eso son 4).    
<p align="center">
<img width="" height="500" alt="image" src="img/image.png" />
</p>

#### 2️⃣ Modificación para usar 3 hilos
Ahora, el programa divide el trabajo en **tres hilos**, cada uno resolviendo un tercio del problema.  

```
public class Main {

	public static void main(String[] args) {
		int max = 500000000;
        int part = max / 3;

        PrimeFinderThread pft1 = new PrimeFinderThread(0, part);
        PrimeFinderThread pft2 = new PrimeFinderThread(part + 1, 2 * part);
        PrimeFinderThread pft3 = new PrimeFinderThread(2 * part + 1, max);

        // Iniciar los tres hilos
        pft1.start();
        pft2.start();
        pft3.start();
    }
	
}
```

✔️ También se evidencia el uso de **4 núcleos**.  
<p align="center">
<img width="" height="500" alt="image" src="img/image1.png" />
</p>   


### 3️⃣ Pausar hilos cada 5 segundos ⏸️➡️▶️  

Se debe pausar la ejecución cada **5 segundos**, mostrar la cantidad de primos encontrados hasta ese momento y esperar a que el usuario presione **ENTER** para reanudar.  

🔧 Cambios realizados:  
- Se añadió un atributo `paused`.  
- Se modificó el método `run()` para validar si el hilo está en pausa.  
- Se implementaron los métodos `pauseThread()` y `resumeThread()`.  

```
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
```
📌 En `Main`, se implementó la lógica de pausa/reanudación:  
```
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
```

## 📌Parte II 
### 🏁 Carrera de Galgos Concurrentes  

#### 1️⃣ Mostrar resultados solo al finalizar todos los hilos  

Se corrigió la aplicación para que el aviso de resultados aparezca únicamente cuando todos los hilos “galgo” hayan terminado.  

✔️ Solución: uso de `join()` en `MainCanodromo`:  
 
```
for (int i = 0; i < can.getNumCarriles(); i++) {
	try {
	galgos[i].join();
	} catch (InterruptedException ex) {
		ex.printStackTrace();
	}
}
can.winnerDialog(reg.getGanador(),reg.getUltimaPosicionAlcanzada() - 1);
System.out.println("El ganador fue:" + reg.getGanador());
```

#### 2️⃣ Identificación de inconsistencias ⚠️  

Al correr varias veces, se presentan inconsistencias en el *ranking* mostrado en consola.  

📌 **Regiones críticas detectadas:**  
- Método `corra()` de la clase **Galgo**.  
- Asignación de posición en el registro de llegada (`ultimaPosicionAlcanzada`).  

Dos o más hilos pueden leer la misma posición antes de que se incremente, provocando empates incorrectos.  

<p align="center">
    <img width="339" height="395" alt="image" src="https://github.com/user-attachments/assets/05e6b847-8cfe-4e35-a5c1-e9f4ce77c4ce" />

```
if (paso == carril.size()) {						
	carril.finish();
	int ubicacion=regl.getUltimaPosicionAlcanzada();
	regl.setUltimaPosicionAlcanzada(ubicacion+1);
	System.out.println("El galgo "+this.getName()+" llego en la posicion "+ubicacion);
	if (ubicacion==1){
		regl.setGanador(this.getName());
	}		
}
```
#### 3️⃣ Solución con sincronización 🔒

Se eliminan los setters y se crea un nuevo método sincronizado en el registro de llegada:   
```
public int registraLlegada(String nombreGalgo) {
	int posicion;
	synchronized (this) {
		posicion = ultimaPosicionAlcanzada++;
		if (posicion == 1) {
			ganador = nombreGalgo;
		}
	}	
	return posicion;
}

```
Y en la clase Galgo:   
```
if (paso == carril.size()) {						
	carril.finish();
	int ubicacion=regl.registraLlegada(this.getName()); 
	System.out.println("El galgo "+this.getName()+" llego en la posicion "+ubicacion);
}
```

#### Funcionalidad Pausa/Continuar ⏸️▶️

Para implementar las funcionalidades se crearon dos nuevos atributos y metodos en la clase galgo: __lock__, __paused__, __pauseAll()__ y __continueAll()__ respectivamente.
Estos metodos son estaticos, permitinedo asi, que todos los galgos compartan el mismo objeto de sincronización y estado
```
private static final Object lock = new Object();
	private static boolean paused = false;
.
.
.

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

//Método run()

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
```
Finalmente se completan los __ActionListener()__ del Main:
```
can.setStopAction(
	new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Galgo.pauseAll();
			System.out.println("Carrera pausada!");
		}
	}
);

can.setContinueAction(
	new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			Galgo.continueAll();
			System.out.println("Carrera reanudada!");
		}
	}
);

```

