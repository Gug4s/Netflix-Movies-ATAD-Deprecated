import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    private static SortedList<NetflixShow> shows = new SortedList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String command = "";

        System.out.println("Bem-vindo ao Netflix Manager. Digite um comando.");

        while (!command.equalsIgnoreCase("QUIT")) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            command = parts[0].toUpperCase();

            switch (command) {
                case "LOADF":
                    if (parts.length < 2) System.out.println("Erro: Indique o ficheiro.");
                    else loadFile(parts[1]);
                    break;

                case "LOADD":
                    loadFile("netflix_movies\\netflix_titles.csv");
                    break;

                case "CLEAR":
                    shows = new SortedList<>();
                    System.out.println("Lista limpa.");
                    break;

                case "LIST":
                    listShows(scanner);
                    break;

                case "GET":
                    if (parts.length < 2) System.out.println("Erro: Indique o show_id.");
                    else getShow(parts[1]);
                    break;

                case "DEL":
                    if (parts.length < 2) System.out.println("Erro: Indique o show_id.");
                    else deleteShow(parts[1]);
                    break;
                
                case "STATS":
                    showStats();
                    break;

                case "MTIME":
                    if (parts.length < 3) System.out.println("Erro: MTIME <min> <max>");
                    else filterDuration("Movie", parts[1], parts[2]);
                    break;

                case "TSEAS":
                    if (parts.length < 3) System.out.println("Erro: TSEAS <min> <max>");
                    else filterDuration("TV Show", parts[1], parts[2]);
                    break;

                case "RATED":
                    if (parts.length < 2) System.out.println("Erro: RATED <rating>");
                    else filterByRating(parts[1]);
                    break;

                case "AYEAR":
                    if (parts.length < 2) System.out.println("Erro: AYEAR <year>");
                    else filterByAddedYear(parts[1]);
                    break;

                case "QUIT":
                    System.out.println("A sair...");
                    break;

                default:
                    System.out.println("Comando desconhecido.");
            }
        }
        scanner.close();
    }

    private static void loadFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            int count = 0;
            
            while ((line = br.readLine()) != null) {
                String[] data = parseCsvLine(line);
                if (data.length >= 11) {
                    try {
                        int releaseYear = Integer.parseInt(data[7].trim());
                        NetflixShow show = new NetflixShow(
                            data[0], data[1], data[2],
                            data[6], releaseYear, data[8], data[9]
                        );
                        shows.add(show);
                        count++;
                    } catch (Exception e) {

                    }
                }
            }
            System.out.println(count + " shows carregados.");
        } catch (Exception e) {
            System.out.println("Erro ao ler ficheiro: " + e.getMessage());
        }
    }

    private static void listShows(Scanner scanner) {
        if (shows.isEmpty()) {
            System.out.println("Lista vazia.");
            return;
        }

        int pageSize = 30;
        int total = shows.size();
        printHeader();

        for (int i = 0; i < total; i++) {
            System.out.println(shows.get(i));

            if ((i + 1) % pageSize == 0) {
                System.out.println("--- Pressione ENTER para continuar ou 'q' para parar ---");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("q")) break;
                printHeader();
            }
        }
    }

    private static void getShow(String id) {
        for (int i = 0; i < shows.size(); i++) {
            NetflixShow s = shows.get(i);
            if (s.getShowId().equalsIgnoreCase(id)) {
                printHeader();
                System.out.println(s);
                return;
            }
        }
        System.out.println("Show ID não encontrado.");
    }

    private static void deleteShow(String id) {
        NetflixShow toRemove = null;
        for (int i = 0; i < shows.size(); i++) {
            NetflixShow s = shows.get(i);
            if (s.getShowId().equalsIgnoreCase(id)) {
                toRemove = s;
                break;
            }
        }

        if (toRemove != null) {
            if (shows.remove(toRemove)) {
                System.out.println("Show " + id + " removido com sucesso.");
            } else {
                System.out.println("Erro ao remover.");
            }
        } else {
            System.out.println("Show ID não encontrado.");
        }
    }

    private static void showStats() {
        if (shows.isEmpty()) {
            System.out.println("Lista vazia.");
            return;
        }
        
        int movies = 0, tvShows = 0;
        int totalMin = 0, totalSeasons = 0;
        
        for (int i = 0; i < shows.size(); i++) {
            NetflixShow s = shows.get(i);
            if (s.getType().equalsIgnoreCase("Movie")) {
                movies++;
                totalMin += s.getDuration();
            } else if (s.getType().equalsIgnoreCase("TV Show")) {
                tvShows++;
                totalSeasons += s.getDuration();
            }
        }
        
        System.out.println("Movie count: " + movies);
        System.out.println("TV Show count: " + tvShows);
        System.out.println("Total movie time: " + totalMin + " min");
        System.out.println("Total tv seasons: " + totalSeasons + " seasons");
    }

    private static void filterDuration(String type, String minStr, String maxStr) {
        try {
            int min = Integer.parseInt(minStr);
            int max = Integer.parseInt(maxStr);
            printHeader();
            int count = 0;

            for (int i = 0; i < shows.size(); i++) {
                NetflixShow s = shows.get(i);
                if (s.getType().equalsIgnoreCase(type) && s.getDuration() >= min && s.getDuration() <= max) {
                    System.out.println(s);
                    count++;
                }
            }
            if (count == 0) System.out.println("Nenhum resultado encontrado.");

        } catch (NumberFormatException e) {
            System.out.println("Erro: Os valores mínimo e máximo devem ser números inteiros.");
        }
    }

    private static void filterByRating(String rating) {
        printHeader();
        int count = 0;
        for (int i = 0; i < shows.size(); i++) {
            NetflixShow s = shows.get(i);
            if (s.getRating() != null && s.getRating().equalsIgnoreCase(rating)) {
                System.out.println(s);
                count++;
            }
        }
        if (count == 0) System.out.println("Nenhum resultado com rating " + rating);
    }

    private static void filterByAddedYear(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr);
            printHeader();
            int count = 0;
            for (int i = 0; i < shows.size(); i++) {
                NetflixShow s = shows.get(i);
                if (s.getAddedYear() == year) {
                    System.out.println(s);
                    count++;
                }
            }
            if (count == 0) System.out.println("Nenhum show adicionado em " + year);

        } catch (NumberFormatException e) {
            System.out.println("Erro: O ano deve ser um número.");
        }
    }

    private static void printHeader() {
        System.out.println("ID     | Type     | Title                          | Date Added   | Rate  | Duration");
        System.out.println("-------|----------|--------------------------------|--------------|-------|----------");
    }

    private static String[] parseCsvLine(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for(int i=0; i<parts.length; i++) {
            parts[i] = parts[i].replace("\"", "").trim();
        }
        return parts;
    }
}