package Servidor;

/**
 * La clase NodoPila representa un nodo en una pila que contiene datos de tipo NodoArbol.
 * Cada nodo contiene un dato de tipo NodoArbol y una referencia al siguiente nodo en la pila.
 */
public class NodoPila {
    /** El dato almacenado en este nodo de la pila. */
    NodoArbol dato;

    /** El siguiente nodo en la pila. */
    NodoPila siguiente;

    /**
     * Crea un nuevo nodo de pila con el dato especificado y sin referencia al siguiente nodo.
     *
     * @param x El dato de tipo NodoArbol que se almacenará en este nodo.
     */
    public NodoPila(NodoArbol x) {
        dato = x;
        siguiente = null;
    }
}
