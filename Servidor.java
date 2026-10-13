import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class Servidor {
    private static final int PORTA = 5000;
    private static final CopyOnWriteArrayList<Jogador> jogadores =
            new CopyOnWriteArrayList<>();
    private static final Object travaJogo = new Object();
    private static int numeroSecreto;
    private static boolean encerrado = false;
    private static String mensagemFinal;

    public static void main(String[] args) throws IOException {
        numeroSecreto = new Random().nextInt(1001);

        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            System.out.println("Servidor rodando na porta " + PORTA + " (Número secreto oculto gerado).");
            while (true) {
                Socket socket = servidor.accept();
                Jogador jogador = new Jogador(socket);
                new Thread(jogador).start();
            }
        }
    }

    private static void enviarParaTodos(String mensagem) {
        for (Jogador j : jogadores) {
            j.enviar(mensagem);
        }
    }

    private static class Jogador implements Runnable {
        private final Socket socket;
        private final PrintWriter saida;
        private final String ip;
        private final String hostname;

        Jogador(Socket socket) throws IOException {
            this.socket = socket;
            this.saida = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);
            this.ip = socket.getInetAddress().getHostAddress();
            this.hostname = socket.getInetAddress().getHostName();
        }

        synchronized void enviar(String mensagem) {
            saida.println(mensagem);
        }

        @Override
        public void run() {
            try (Socket conexao = socket;
                 BufferedReader entrada = new BufferedReader(new InputStreamReader(
                         conexao.getInputStream(), StandardCharsets.UTF_8))) {

                synchronized (travaJogo) {
                    if (encerrado) {
                        enviar(mensagemFinal);
                        return;
                    }
                    jogadores.add(this);
                    enviar("BEM_VINDO Digite um número entre 0 e 1000");
                }

                String linha;
                while ((linha = entrada.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.equalsIgnoreCase("SAIR")) {
                        break;
                    }

                    int palpite;
                    try {
                        palpite = Integer.parseInt(linha);
                    } catch (NumberFormatException e) {
                        enviar("ERRO Digite um inteiro entre 0 e 1000");
                        continue;
                    }

                    if (palpite < 0 || palpite > 1000) {
                        enviar("ERRO Digite um inteiro entre 0 e 1000");
                        continue;
                    }

                    synchronized (travaJogo) {
                        if (encerrado) {
                            break;
                        }

                        if (palpite < numeroSecreto) {
                            enviar("MAIOR");
                        } else if (palpite > numeroSecreto) {
                            enviar("MENOR");
                        } else {
                            encerrado = true;
                            mensagemFinal = "VENCEDOR IP=" + ip + " HOST=" + hostname + " NUMERO=" + numeroSecreto;
                            enviarParaTodos(mensagemFinal);

                            for (Jogador j : jogadores) {
                                try {
                                    j.socket.close();
                                } catch (IOException ignored) {}
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Conexão encerrada: " + ip);
            } finally {
                jogadores.remove(this);
            }
        }
    }
}
