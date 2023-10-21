package Servidor;

/**
 * La clase PilaArbolExp representa una pila que almacena nodos de un árbol.
 */
public class PilaArbolExp {
    private NodoPila tope;

    /**
     * Constructor de la clase PilaArbolExp.
     */
    public PilaArbolExp() {
        tope = null;
    }

    /**
     * Inserta un nodo de árbol en la parte superior de la pila.
     *
     * @param elemento El nodo de árbol que se va a insertar.
     */
    public void insertar(NodoArbol elemento) {
        NodoPila nuevo = new NodoPila(elemento);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    /**
     * Verifica si la pila está vacía.
     *
     * @return true si la pila está vacía, false en caso contrario.
     */
    public boolean pilaVacia() {
        return tope == null;
    }

    /**
     * Obtiene el nodo de árbol en la parte superior de la pila.
     *
     * @return El nodo de árbol en la parte superior de la pila.
     */
    public NodoArbol topePila() {
        return tope.dato;
    }

    /**
     * Reinicia la pila, eliminando todos los elementos.
     */
    public void ReiniciarPila() {
        tope = null;
    }

    /**
     * Quita y devuelve el nodo de árbol en la parte superior de la pila.
     *
     * @return El nodo de árbol eliminado de la parte superior de la pila.
     */
    public NodoArbol quitar() {
        NodoArbol aux = null;
        if (!pilaVacia()) {
            aux = tope.dato;
            tope = tope.siguiente;
        }
        return aux;
    }
}

