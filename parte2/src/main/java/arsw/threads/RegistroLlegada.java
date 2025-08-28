package arsw.threads;

public class RegistroLlegada {

	private int ultimaPosicionAlcanzada=1;

	private String ganador=null;

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

	public String getGanador() {
		return ganador;
	}

	public int getUltimaPosicionAlcanzada() {
		return ultimaPosicionAlcanzada;
	}
}
