import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente {
    private static volatile boolean encerrado = false;

    public static void main(String[] args) throws IOException {
        String servidor = args.length > 0 ? args[0] : "localhost";
        try (Socket socket = new Socket(servidor, 5000);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter saida = new PrintWriter(new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner teclado = new Scanner(System.in)) {

            Thread receptor = new Thread(() -> {
                try {
                    String msgServidor;
                    while ((msgServidor = entrada.readLine()) != null) {
                        System.out.println(msgServidor);
                        if (msgServidor.startsWith("VENCEDOR")) {
                            encerrado = true;
                        }
                    }
                } catch (Exception e) {
                } finally {
                    encerrado = true;
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            System.out.println("Digite um número de 0 a 1000 ou SAIR.");
            while (!encerrado && teclado.hasNextLine()) {
                String comando = teclado.nextLine();
                if (comando.trim().isEmpty()) {
                    continue;
                }
                
                saida.println(comando);
                
                if (comando.equalsIgnoreCase("SAIR")) {
                    break;
                }
            }
        }
    }
}
