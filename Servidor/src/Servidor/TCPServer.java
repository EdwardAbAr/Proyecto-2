package Servidor;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(12345);
            System.out.println("Servidor escuchando en el puerto 12345...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado desde: " + clientSocket.getInetAddress());

                // Configurar los streams de entrada y salida
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Leer los operandos y el operador del cliente
                double operand1 = Double.parseDouble(in.readLine());
                String operator = in.readLine();
                double operand2 = Double.parseDouble(in.readLine());

                // Realizar la operación
                double result;
                switch (operator) {
                    case "+":
                        result = operand1 + operand2;
                        break;
                    case "-":
                        result = operand1 - operand2;
                        break;
                    case "*":
                        result = operand1 * operand2;
                        break;
                    case "/":
                        if (operand2 == 0) {
                            out.println("Operación inválida: División por cero.");
                            clientSocket.close();
                            continue;
                        }
                        result = operand1 / operand2;
                        break;
                    default:
                        out.println("Operación inválida: Operador no reconocido.");
                        clientSocket.close();
                        continue;
                }

                // Enviar la operación y el resultado al cliente
                out.println("Operación: " + operand1 + " " + operator + " " + operand2);
                out.println("Resultado: " + result);

                // Cerrar la conexión con el cliente
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


