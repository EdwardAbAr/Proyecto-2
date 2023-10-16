
package Servidor;

public class ArbolBinarioExp {
    NodoArbol raiz;

    public  ArbolBinarioExp(){
        raiz = null;
    }
    public  ArbolBinarioExp(String expresion){
        raiz = creaArbolBE(expresion);
    }
    public void reinciarArbol(){
        raiz = null;
    }
    public void crearNodo(Object dato){
        raiz = new NodoArbol(dato);
    }
    public NodoArbol crearSubArbol(NodoArbol dato2, NodoArbol dato1, NodoArbol operador){
        operador.izquierdo = dato1;
        operador.derecho = dato2;
        return operador;
    }
    public boolean arbolVacio(){
        return raiz == null;
    }
    private String preorden(NodoArbol subArbol, String c){
        String expresion;
        expresion = "";
        if(subArbol != null){
            expresion = c + subArbol.dato.toString()+"\n"+preorden(subArbol.izquierdo, c)+preorden(subArbol.derecho, c);
        }
        return expresion;
    }
    private String inorden(NodoArbol subArbol, String c){
        String cadena;
        cadena = "";
        if (subArbol !=null){
            cadena = c + inorden(subArbol.izquierdo, c)+subArbol.dato.toString()+"\n"+inorden(subArbol.derecho, c);
        }
        return cadena;
    }
    private String posorden(NodoArbol subArbol, String c){
        String cadena;
        cadena = "";
        if (subArbol !=null){
            cadena = c + posorden(subArbol.izquierdo, c)+posorden(subArbol.derecho, c)+ subArbol.dato.toString()+"\n";
        }
        return cadena;
    }
    public String toSring(int a){
        String cadena = "";
        switch (a){
            case 0:
                cadena = preorden(raiz, cadena);
                break;
            case 1:
                cadena = inorden(raiz, cadena);
                break;
            case 2:
                cadena = posorden(raiz, cadena);
                break;
        }
        return cadena;
    }
    private int prioridad(char c){
        int p = 100;
        switch (c){
            case'y':
                p=30;
                break;
            case '*':
            case '/':
                p=20;
                break;
            case '+':
            case '-':
                p=10;
                break;
            default:
                p=0;
        }
        return p;
    }
    private boolean esOperador(char c){
        boolean resultado;
        switch (c){
            case'(':
            case')':
            case'y':
            case'*':
            case'/':
            case'+':
            case'-':
                resultado = true;
            default:
                resultado=false;
        }
        return resultado;
    }
    private NodoArbol creaArbolBE(String cadena){
        PilaArbolExp PilaOperadores;
        PilaArbolExp PilaExpresiones;
        NodoArbol token;
        NodoArbol op1;
        NodoArbol op2;
        NodoArbol op;
        PilaOperadores = new PilaArbolExp();
        PilaExpresiones = new PilaArbolExp();
        boolean bandera = false;
        char caracterEvaluado;
        for (int i=0; i<cadena.length();i++){
            caracterEvaluado = cadena.charAt(i);
            token = new NodoArbol(caracterEvaluado);
            if (!esOperador(caracterEvaluado)){
                if (!bandera){
                    bandera = true;
                    PilaExpresiones.insertar(token);
                }else{
                    String aux = PilaExpresiones.quitar().dato.toString();
                    aux = aux + caracterEvaluado;
                    token = new NodoArbol(aux);
                    PilaExpresiones.insertar(token);
                }

            }
            else{
                bandera = false;
                switch (caracterEvaluado) {
                    case '(':
                        PilaOperadores.insertar(token);
                        break;
                    case ')':
                        while (!PilaOperadores.pilaVacia() && !PilaOperadores.topePila().dato.equals('(')) {
                            op2 = PilaExpresiones.quitar();
                            op1 = PilaExpresiones.quitar();
                            op = PilaOperadores.quitar();
                            op = crearSubArbol(op2, op1, op);
                            PilaExpresiones.insertar(op);
                        }
                        PilaOperadores.quitar();
                        break;
                    default:
                        while (!PilaOperadores.pilaVacia() && prioridad(caracterEvaluado) <= prioridad(PilaOperadores.topePila().dato.toString().charAt(0))) {
                            op2 = PilaExpresiones.quitar();
                            op1 = PilaExpresiones.quitar();
                            op = PilaOperadores.quitar();
                            op = crearSubArbol(op2, op1, op);
                            PilaExpresiones.insertar(op);;
                        }
                        PilaOperadores.insertar(token);

                }

            }
        }
        while (!PilaOperadores.pilaVacia()){
            op2 = PilaExpresiones.quitar();
            op1 = PilaExpresiones.quitar();
            op = PilaOperadores.quitar();
            op = crearSubArbol(op2, op1, op);
            PilaExpresiones.insertar(op);
        }
        op = PilaExpresiones.quitar();
        return op;
    }
    public double EvaluaExpresion(String expresion) {
        raiz = creaArbolBE(expresion);
        return evalua(raiz);
    }

    private double evalua(NodoArbol subArbol) {
        if (!esOperador(subArbol.dato.toString().charAt(0))) {
            return Double.parseDouble(subArbol.dato.toString());
        } else {
            double acum = 0; // Initialize acum for each call to evalua
            switch (subArbol.dato.toString().charAt(0)) {
                case 'y':
                    acum = Math.pow(evalua(subArbol.izquierdo), evalua(subArbol.derecho));
                    break;
                case '*':
                    acum = evalua(subArbol.izquierdo) * evalua(subArbol.derecho);
                    break;
                case '/':
                    acum = evalua(subArbol.izquierdo) / evalua(subArbol.derecho);
                    break;
                case '+':
                    acum = evalua(subArbol.izquierdo) + evalua(subArbol.derecho);
                    break;
                case '-':
                    acum = evalua(subArbol.izquierdo) - evalua(subArbol.derecho);
                    break;
            }
            return acum;
        }
    }


}
