package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.*;

import entities.Compra;
import entities.cliente.Cliente;
import entities.cliente.TipoCliente;
import entities.jogo.CategoriaJogo;
import entities.jogo.Jogo;
import util.Data;

public class Sistema {

    public static final String ARQ_DADOS = "clientes.bin";
    static Scanner teclado = new Scanner(System.in);

    private static List<Jogo> jogos = new ArrayList<>();
    private static TreeMap<String, Cliente> clientes = new TreeMap<>();
    private static List<Compra> compras = new ArrayList<>();

    static LocalDate today = LocalDate.now();
    static Data hoje = new Data(today.getDayOfMonth(), today.getMonthValue());

    // #region Métodos do MENU
    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa(Scanner teclado) {
        System.out.println("Enter para continuar.");
        teclado.nextLine();
    }

    public static int menu(Scanner teclado) {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== OPERAÇÕES =======");
        System.out.println("1 - Cadastrar Cliente");
        System.out.println("2 - Registrar Compra");
        System.out.println("3 - Obter Histórico do Cliente");
        System.out.println("======== CONSULTAS =======");
        System.out.println("4 - Valor Mensal Vendido");
        System.out.println("5 - Valor Médio das Compras");
        System.out.println("6 - Jogo mais Vendido");
        System.out.println("7 - Jogo menos Vendido");
        System.out.println("0 - Sair");
        int opcao = 0;
        try {
            opcao = teclado.nextInt();
            teclado.nextLine();
        } catch (InputMismatchException ex) {
            teclado.nextLine();
            System.out.println("Somente opções numéricas.");
            opcao = -1;
        }
        return opcao;
    }

    private static void menu1CadastroDeCliente() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 1 - Cadastrar Cliente =======");
        System.out.println("\nDigite o Nome do Cliente");
        String nome = teclado.nextLine();
        System.out.println("\nDigite o Nome de Usuário do Cliente");
        String username = teclado.nextLine();
        System.out.println("\nDigite a Senha do Usuário");
        String senha = teclado.nextLine();
        System.out.println("\nSelecione o tipo do Cliente");
        System.out.println("\n1 - Cliente CADASTRADO");
        System.out.println("\n2 - Cliente EMPOLGADO");
        System.out.println("\n3 - Cliente FANÁTICO");
        TipoCliente tipoCliente = TipoCliente.CADASTRADO;
        int nTipoCliente = teclado.nextInt();
        switch (nTipoCliente) {
            case 2:
                tipoCliente = TipoCliente.EMPOLGADO;
                break;
            case 3:
                tipoCliente = TipoCliente.FANATICO;
                break;
            case 1:
            default:
                break;
        }
        clientes.put(nome, new Cliente(nome, username, senha, tipoCliente));
        teclado.nextLine();
    }

    private static void menu2RegistrarCompra() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 2 - Registrar Compra =======");
        System.out.println("\nDigite o nome de usuário do cliente");
        Cliente cliente = buscarCliente(teclado.nextLine());
        if (cliente == null) {
            System.out.println("Cliente não Registrado!");
            System.out.println("\n Deseja repetir a busca?");
            System.out.println("1 - SIM");
            System.out.println("2 - NÃO");
            teclado.nextLine();
            int opcao = -1;

            try {
                opcao = teclado.nextInt();
                teclado.nextLine();
            } catch (InputMismatchException ex) {
                teclado.nextLine();
            }
            if (opcao == 1)
                menu2RegistrarCompra();
            else
                menu(teclado);
        }
        ArrayList<Jogo> jogosComprados = menu2RegistrarCompra_Jogos();

        Compra novaCompra = cliente.comprar(jogosComprados, hoje);
        System.out.println(novaCompra);
    }

    private static ArrayList<Jogo> menu2RegistrarCompra_Jogos() {
        ArrayList<Jogo> jogosComprados = new ArrayList<>();
        System.out.println("Digite a quantidade de jogos da compra");
        int qtdJogos = teclado.nextInt();
        for (int i = 0; i < qtdJogos; i++) {
            System.out.println("Digite o nome do Jogo: ");
            teclado.nextLine();
            String nomeJogo = teclado.nextLine();
            Jogo jogoProcurado = buscarJogo(nomeJogo);
            if (jogoProcurado != null) {
                jogosComprados.add(jogoProcurado);
            } else {
                System.out.println("Jogo não encontrado!");
                System.out.println("\n Deseja repetir a busca de todos os jogos?");
                System.out.println("1 - SIM");
                System.out.println("2 - NÃO");
                int opcao = teclado.nextInt();
                if (opcao == 1) {
                    menu2RegistrarCompra_Jogos();
                } else {
                    return null;
                }
            }
        }
        return jogosComprados;
    }

    private static void menu3ObterHistoricoDoCliente() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 3 - Obter Histórico do Cliente =======");
        System.out.println("\nDigite o nome de usuário do Cliente");
        Cliente cliente = buscarCliente(teclado.nextLine());
        if (cliente == null) {
            System.out.println("Cliente não Registrado!");
            System.out.println("\n Deseja repetir a busca?");
            System.out.println("1 - SIM");
            System.out.println("2 - NÃO");
            int opcao = -1;

            try {
                opcao = teclado.nextInt();
                teclado.nextLine();
            } catch (InputMismatchException ex) {
                teclado.nextLine();
            }
            if (opcao == 1)
                menu3ObterHistoricoDoCliente();
            else
                menu(teclado);
        } else {
            System.out.println(cliente.historicoCompleto());
        }
    }

    private static void menu4ValorMensalVendido() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 4 - Valor Mensal Vendido =======");
        System.out.println("Informe o mês e o ano buscado (Ex: 06/2022)");
        String[] str = teclado.nextLine().split("/");
        int mes = Integer.parseInt(str[0]);
        int ano = Integer.parseInt(str[1]);
        double valorMensalProcurado = valorMensalVendido(mes, ano);
        System.out.println("O valor mensal vendido de " + mes + "/" + ano + " foi de R$" + valorMensalProcurado);
        System.out.println("\nPressione qualquer tecla para voltar ao menu principal");
        teclado.nextLine();
        menu(teclado);
    }

    private static void menu5ValorMedioDasCompras() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 5 - Valor Médio das Compras =======");
        double valorMedio = valorMedioCompras();
        System.out.println("O valor médio das compras é de: R$" + valorMedio);
        System.out.println("\nPressione qualquer tecla para voltar ao menu principal");
        teclado.nextLine();
        menu(teclado);
    }

    private static void menu6JogoMaisVendido() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 6 - Jogo Mais Vendido =======");
        Jogo maisVendido = jogoMaisVendido();
        System.out.println("O Jogo mais vendido é: " + maisVendido);
        System.out.println("\nPressione qualquer tecla para voltar ao menu principal");
        teclado.nextLine();
        menu(teclado);
    }

    private static void menu7JogoMenosVendido() {
        limparTela();
        System.out.println("XULAMBS GAMES");
        System.out.println("======== 7 - Jogo Menos Vendido =======");
        Jogo menosVendido = jogoMenosVendido();
        System.out.println("O Jogo menos vendido é: " + menosVendido);
        System.out.println("\nPressione qualquer tecla para voltar ao menu principal");
        teclado.nextLine();
        menu(teclado);
    }

    // #endregion

    public static void main(String[] args) {

        // inicializacao: leitura de dados do arquivo
        clientes = lerClientes(ARQ_DADOS);

        jogos.add(new Jogo("BTD6", CategoriaJogo.LANCAMENTO, 100, 110));
        jogos.add(new Jogo("Overcooked", CategoriaJogo.REGULAR, 70, 70));

        // c.comprar(jogos, hoje);

        // c1.comprar(jogos, hoje);

        // clientes.put(c.getNomeUsuario(), c);
        // clientes.put(c1.getNomeUsuario(), c1);

        int opcao;
        do {

            opcao = menu(teclado);

            switch (opcao) {
                case 1:
                    menu1CadastroDeCliente();
                    break;
                case 2:
                    menu2RegistrarCompra();
                    break;
                case 3:
                    menu3ObterHistoricoDoCliente();
                    break;
                case 4:
                    menu4ValorMensalVendido();
                case 5:
                    menu5ValorMedioDasCompras();
                case 6:
                    menu6JogoMaisVendido();
                case 7:
                    menu7JogoMenosVendido();
            }
            pausa(teclado);
            limparTela();
            teclado.nextLine();
        } while (opcao != 0);

        // testes
        compras = getComprasClientes(clientes);
        for (Compra compra : compras) {
            System.out.println(compra);
        }

        escreverClientes(clientes, ARQ_DADOS);
    }

    // #region Leitura de Clientes
    public static void escreverClientes(TreeMap<String, Cliente> clientes, String nomeArq) {

        try {
            FileOutputStream fout = new FileOutputStream(nomeArq);
            ObjectOutputStream arqSaida = new ObjectOutputStream(fout);
            arqSaida.writeObject(clientes);
            arqSaida.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro na escrita de dados: arquivo não encontrado");
        } catch (IOException e) {
            System.out.println("Erro na escrita de dados: " + e);
        }
    }

    public static TreeMap<String, Cliente> lerClientes(String nomeArq) {

        TreeMap<String, Cliente> clientes = new TreeMap<>();
        FileInputStream arq;
        try {
            arq = new FileInputStream(nomeArq);

            ObjectInputStream arqLeitura;
            arqLeitura = new ObjectInputStream(arq);

            for (Cliente c : clientes.values()) {
                System.out.println(c);
            }

            clientes = (TreeMap<String, Cliente>) arqLeitura.readObject();

            arqLeitura.close();

        } catch (FileNotFoundException e) {
            System.out.println("Erro na leitura de dados: arquivo não encontrado");
        } catch (IOException e) {
            System.out.println("Erro na leitura de dados: " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Erro na leitura de dados: Os dados não estão no formato correto");
        }

        return clientes;
    }

    public static ArrayList<Compra> getComprasClientes(TreeMap<String, Cliente> clientes) {
        ArrayList<Compra> compras = new ArrayList<>();
        for (Cliente c : clientes.values()) {
            compras.addAll(c.getComprasHistorico());
        }
        return compras;
    }
    // #endregion

    // #region Consultas XulambGames

    private static Cliente buscarCliente(String nomeUsuario) {
        return clientes.values().stream().filter(c -> c.getNomeUsuario().equals(nomeUsuario)).findFirst().orElse(null);
    }

    private static Jogo buscarJogo(String nomeJogo) {
        return jogos.stream().filter(j -> j.getNome().equals(nomeJogo)).findFirst().orElse(null);
    }

    public static double valorMensalVendido(int mesProcurado, int anoProcurado) {
        return Math.round(todasAsCompras().stream()
                .filter(c -> c.getData().getMes() == mesProcurado && c.getData().getAno() == anoProcurado)
                .mapToDouble(Compra::getValorPago)
                .sum() * 100.0) / 100.0;
    }

    public static double valorMedioCompras() {
        ArrayList<Compra> todasAsCompras = todasAsCompras();
        double valorTotal = Math.round(todasAsCompras.stream()
                .mapToDouble(Compra::getValorPago)
                .sum() * 100.0) / 100.0;
        return valorTotal/todasAsCompras.size();
    }

    public static Jogo jogoMaisVendido() {

        return jogos.stream()
                .max(Comparator.comparingInt(Jogo::getQtdVendas))
                .orElse(null);
    }

    public static Jogo jogoMenosVendido() {
        return jogos.stream()
                .min(Comparator.comparingInt(Jogo::getQtdVendas))
                .orElse(null);
    }

    public static ArrayList<Compra> todasAsCompras() {
        ArrayList<Compra> todasAsCompras = new ArrayList<>();
        for (Cliente c : clientes.values()) {
            todasAsCompras.addAll(c.getComprasHistorico());
        }
        return todasAsCompras;
    }
    // #endregion
}
