/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Arbol;

/**
 *
 * @author Usuario
 */
public class PilaArbolExp {
    private NodoPila tope;
    
    public PilaArbolExp(){
    
        tope = null;
    
    }
    
    public void insertar(NodoArbol elemento){
    
        NodoPila nuevo;
        nuevo = new NodoPila(elemento);
        nuevo.siguiente = tope;
        tope = nuevo;
    
    }
    
    public boolean pilaVacia(){
    
        return tope == null;
    
    }
    
    public NodoArbol topePila(){
    
        return tope.dato;
    
    }
    
    public void ReiniciarPila(){
    
        tope = null;
    
    }
    
    public NodoArbol quitar(){
    
        NodoArbol aux = null;
        if(!pilaVacia()){

            aux = tope.dato;
            tope = tope.siguiente;
        }
        return aux;
    }
}