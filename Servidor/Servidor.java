package Servidor;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.net.*;

public class Servidor  {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        MarcoServidor mimarco = new MarcoServidor();

        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}

class MarcoServidor extends JFrame implements Runnable{
    private ArbolBinarioExp arbolExp;
    private	JTextArea areatexto;
    public MarcoServidor(){

        setBounds(1200,300,280,350);
        JPanel milamina= new JPanel();
        milamina.setLayout(new BorderLayout());
        areatexto=new JTextArea();
        milamina.add(areatexto,BorderLayout.CENTER);
        add(milamina);
        setVisible(true);
        Thread hilo = new Thread(this);
        hilo.start();
        arbolExp = new ArbolBinarioExp();

    }


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(5678);

            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream entrada = new DataInputStream(socket.getInputStream());
                String expresion = entrada.readUTF();

                // Evaluate the expression using the ArbolBinarioExp instance
                arbolExp.reinciarArbol();
                arbolExp.crearNodo(expresion);  // Create a new root node with the received expression
                double result = arbolExp.EvaluaExpresion();

                // Do something with the result, e.g., print it or send it back to the client
                System.out.println("Received Expression: " + expresion);
                System.out.println("Resultado: " + result);

                areatexto.append("\n" + expresion);  // Add the received expression to the JTextArea
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
