# Redes2TrabalhoN2
# Jogo de Adivinhação (Sockets TCP e Threads)

Desenvolvido individualmente por Árthur para a disciplina de Redes de Computadores II (PUC-GO).

## Como Executar

Abra o terminal na pasta dos arquivos e execute os comandos abaixo:

```bash
javac Servidor.java Cliente.java
java Servidor
java Cliente localhost

```

## Perguntas e respostas
1. Por que o cliente precisa receber mensagens em outra thread?
Porque a thread principal do cliente fica bloqueada esperando a digitação do usuário no terminal (Scanner.nextLine()). Sem uma segunda thread para ler o socket, o cliente não conseguiria receber avisos em tempo real (como o anúncio de vitória) enquanto estivesse aguardando um palpite.

2. O que pode acontecer sem sincronização no servidor?
Ocorre uma condição de corrida (race condition). Se dois clientes acertassem o número secreto no mesmo instante, múltiplas threads poderiam validar a vitória simultaneamente, resultando em mais de um vencedor.

3. Qual é a diferença entre IP, hostname e porta?
O IP é o endereço numérico que identifica a máquina na rede, o hostname é o nome legível associado a ela, e a porta é o número lógico que direciona o tráfego de rede para a aplicação específica no servidor (porta 5000).

4. O que acontece quando readLine() retorna null?
Indica que a conexão foi encerrada de forma limpa ou abrupta pelo outro lado (ex: fechamento do terminal). O servidor utiliza isso para detectar a queda e remover o jogador da lista ativa.
