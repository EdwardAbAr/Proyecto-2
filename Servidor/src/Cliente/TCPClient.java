package Cliente;

import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Cambia esto a la dirección IP del servidor si es diferente
        int serverPort = 12345;

        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Ingresa el primer operando: ");
            double operand1 = Double.parseDouble(userInput.readLine());

            System.out.print("Ingresa el operador (+, -, *, /): ");
            String operator = userInput.readLine();

            System.out.print("Ingresa el segundo operando: ");
            double operand2 = Double.parseDouble(userInput.readLine());

            // Enviar los operandos y el operador al servidor
            out.println(operand1);
            out.println(operator);
            out.println(operand2);

            // Leer la respuesta del servidor
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Respuesta del servidor: " + response);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}