package Arbol;

public class ArbolBinarioExp {
    NodoArbol raiz;
    
    public ArbolBinarioExp(){
    
        raiz = null;
    
    }
    
    public ArbolBinarioExp(String cadena){
       
        raiz = creaArbolBE(cadena);
    
    
    }
    
    public void reiniciarArbol(){
    
        raiz = null;
    
    }
    
    public void creaNodo(Object dato){
    
        raiz = new NodoArbol(dato);
    
    }
    
    public NodoArbol creaSubArbol(NodoArbol dato2, NodoArbol dato1, NodoArbol operador){
        operador.izquierdo = dato1;
        operador.derecho = dato2;
        return operador;
    }

    public boolean arbolVacio(){
        return raiz == null;
    }

     private String preorden(NodoArbol subArbol, String c){
        String cadena;
        cadena = "";
        if (subArbol != null){
            cadena = c + subArbol.dato.toString() + "\n" + preorden(subArbol.izquierdo,c) + preorden(subArbol.derecho,c);
        }
        return cadena;
    }
    
     private String inorden(NodoArbol subArbol, String c){
        String cadena;
        cadena = "";
        if (subArbol != null){
            cadena = c +  inorden(subArbol.izquierdo, c)+ subArbol.dato.toString() + "\n" + inorden(subArbol.izquierdo, c);
        }
        return cadena;
    }
    
    private String posorden(NodoArbol subArbol, String c){
        String cadena;
        cadena = "";
        if (subArbol != null){
            cadena = c +  posorden(subArbol.izquierdo, c) + posorden(subArbol.izquierdo, c) + subArbol.dato.toString() + "\n";
        }
        return cadena;
    }

    public String toString(int a){
        String cadena = "";
        switch(a){
            case 0 -> cadena = preorden(raiz,cadena);
            case 1 -> cadena = preorden(raiz,cadena);
            case 2 -> cadena = posorden(raiz,cadena);
       
        }
        return cadena;
    }
    
 
    private int prioridad(char c){
        int p = 100;
        p = switch(c){
            case '^' -> 30;
            case'*','/' -> 20;
            case '+','-' -> 10;
            default -> 0;
        };
        return p;
    }

    private boolean esOperador(char c){
        boolean resultado;
        resultado = switch(c){
            case '(',')','^','*','/','+','-' -> true;
            default -> false;
        };
        return resultado;

    }

    private NodoArbol creaArbolBE(String cadena) {
    PilaArbolExp PilaOperadores = new PilaArbolExp();
    PilaArbolExp PilaExpresiones = new PilaArbolExp();

    for (int i = 0; i < cadena.length(); i++) {
        char caracterEvaluado = cadena.charAt(i);
        if (caracterEvaluado == '(') {
            PilaOperadores.insertar(new NodoArbol(caracterEvaluado));
        } else if (caracterEvaluado == ')') {
            // Desapilar y construir subárbol hasta encontrar un '('
            while (!PilaOperadores.pilaVacia() && PilaOperadores.topePila().dato.toString().charAt(0) != '(') {
                NodoArbol operador = PilaOperadores.quitar();
                NodoArbol dato2 = PilaExpresiones.quitar();
                NodoArbol dato1 = PilaExpresiones.quitar();
                NodoArbol subArbol = creaSubArbol(dato2, dato1, operador);
                PilaExpresiones.insertar(subArbol);
            }
            // Desapilar el '('
            PilaOperadores.quitar();
        } else if (!esOperador(caracterEvaluado)) {
            PilaExpresiones.insertar(new NodoArbol(caracterEvaluado));
        } else {
            // Operadores como +, -, *, /, ^
            while (!PilaOperadores.pilaVacia() && prioridad(caracterEvaluado) <= prioridad(PilaOperadores.topePila().dato.toString().charAt(0))) {
                NodoArbol operador = PilaOperadores.quitar();
                NodoArbol dato2 = PilaExpresiones.quitar();
                NodoArbol dato1 = PilaExpresiones.quitar();
                NodoArbol subArbol = creaSubArbol(dato2, dato1, operador);
                PilaExpresiones.insertar(subArbol);
            }
            PilaOperadores.insertar(new NodoArbol(caracterEvaluado));
        }
    }

    // Desapilar cualquier operador restante
    while (!PilaOperadores.pilaVacia()) {
        NodoArbol operador = PilaOperadores.quitar();
        NodoArbol dato2 = PilaExpresiones.quitar();
        NodoArbol dato1 = PilaExpresiones.quitar();
        NodoArbol subArbol = creaSubArbol(dato2, dato1, operador);
        PilaExpresiones.insertar(subArbol);
    }

    // El resultado final es el único elemento en PilaExpresiones
    NodoArbol resultado = PilaExpresiones.quitar();
    return resultado;
    
    }
    
    public double EvaluaExpresion(){
        return evalua(raiz);
    }

    private double evalua(NodoArbol subArbol){
        double acum = 0;
        if(!esOperador(subArbol.dato.toString().charAt(0))){
            return Double.parseDouble(subArbol.dato.toString());
        }else{
            switch(subArbol.dato.toString().charAt(0)){
                case '^' -> acum = acum + Math.pow(evalua(subArbol.izquierdo),evalua(subArbol.derecho));
                case '*' -> acum = acum + evalua(subArbol.izquierdo)* evalua(subArbol.derecho);
                case '/' -> acum = acum + evalua(subArbol.izquierdo) / evalua(subArbol.derecho);
                case '+' -> acum = acum + evalua(subArbol.izquierdo) + evalua(subArbol.derecho);
                case '-' -> acum = acum + evalua(subArbol.izquierdo) - evalua(subArbol.derecho);
            }   
            return acum;
        }
    }
}