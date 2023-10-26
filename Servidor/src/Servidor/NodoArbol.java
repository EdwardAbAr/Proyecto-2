package Servidor;

/**
 * La clase NodoArbol representa un nodo en un árbol binario.
 * Cada nodo contiene un dato, así como referencias a sus hijos izquierdo y derecho.
 */
public class NodoArbol {
    /** El dato almacenado en este nodo. */
    Object dato;

    /** Referencia al hijo izquierdo de este nodo. */
    NodoArbol izquierdo;

    /** Referencia al hijo derecho de este nodo. */
    NodoArbol derecho;

    /**
     * Constructor de la clase NodoArbol.
     *
     * @param x El dato que se almacenará en este nodo.
     */
    public NodoArbol(Object x) {
        dato = x;
        izquierdo = null;
        derecho = null;
    }
}

