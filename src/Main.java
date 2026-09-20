import java.util.Scanner;

public class Main {

    public static int getIntInput(Scanner sc) {
        while (true) {
            try {
                return sc.nextInt();
            } catch (Exception e) {
                sc.nextLine();
                System.out.println("Invalid input! Please enter a number.");
                System.out.print("Choose: ");
            }
        }
    }

    // Same idea as getIntInput, but for decimal fields (price, credit limit, payment amount)
    // that were previously read with a raw sc.nextDouble() — those crashed the whole
    // program with an uncaught InputMismatchException if the user typed a non-number.
    public static double getDoubleInput(Scanner sc) {
        while (true) {
            try {
                return sc.nextDouble();
            } catch (Exception e) {
                sc.nextLine();
                System.out.print("Invalid input! Please enter a valid number: ");
            }
        }
    }

    // Our CSV files are comma-delimited with no quoting, so a comma inside a
    // user-typed field (a product name, username, or password) would shift every
    // field after it when the row is split(",") back apart on load. Reject it at
    // the point of entry instead.
    public static boolean hasComma(String s) {
        return s.contains(",");
    }

    // Always show peso amounts with exactly 2 decimal places instead of Java's
    // default double formatting (which prints "100.0" instead of "100.00").
    public static String money(double amount) {
        return String.format("%.2f", amount);
    }

    // Prints rows as a bordered table. Column widths are computed from the data, so it
    // stays aligned no matter how long a name or amount is. rightAlign[i] = true right-
    // aligns column i (use it for numbers/money). Plain ASCII borders are used on purpose
    // so the table lines up in every console, including ones that mangle box-drawing chars.
    public static void printTable(String[] headers, boolean[] rightAlign, java.util.List<String[]> rows) {
        int cols = headers.length;
        int[] width = new int[cols];
        for (int i = 0; i < cols; i++) width[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < cols; i++) width[i] = Math.max(width[i], row[i].length());
        }

        StringBuilder line = new StringBuilder("+");
        for (int i = 0; i < cols; i++) line.append("-".repeat(width[i] + 2)).append("+");
        String border = line.toString();

        System.out.println(border);
        printTableRow(headers, width, new boolean[cols]); // headers are always left-aligned
        System.out.println(border);
        for (String[] row : rows) printTableRow(row, width, rightAlign);
        System.out.println(border);
    }

    private static void printTableRow(String[] cells, int[] width, boolean[] rightAlign) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < width.length; i++) {
            String fmt = rightAlign[i] ? " %" + width[i] + "s |" : " %-" + width[i] + "s |";
            sb.append(String.format(fmt, cells[i]));
        }
        System.out.println(sb);
    }

    // ANSI escape codes — supported by most modern terminals (macOS Terminal, most
    // Linux terminals, Windows Terminal, VS Code's integrated terminal, IntelliJ's
    // console). Older plain Windows cmd.exe windows may not honor these and will
    // just print the raw escape characters instead of actually clearing.
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printBanner() {
        System.out.println("========================================");
        System.out.println("          TindaLista Prototype System");
        System.out.println("========================================");
        System.out.println();
    }

    // Waits for the user to press Enter, then clears the screen and reprints the
    // banner, so every screen starts from the same clean state instead of
    // scrolling endlessly downward.
    public static void pauseAndClear(Scanner sc) {
        sc.nextLine(); // flush the leftover newline from whatever was read last
        System.out.println();
        System.out.println("Press Enter to continue...");
        sc.nextLine();
        clearScreen();
        printBanner();
    }

    public static void main(String[] args) {
        Store store = new Store();
        store.loadAll();

        AccountManager accountManager = store.getAccountManager();

        Scanner sc = new Scanner(System.in);
        int choice;

        printBanner();

        do {
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            choice = getIntInput(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("New username: ");
                    String u = sc.next();
                    System.out.print("New password: ");
                    String p = sc.next();
                    if (hasComma(u) || hasComma(p)) {
                        System.out.println("Username and password can't contain commas.");
                        pauseAndClear(sc);
                        break;
                    }
                    boolean ok = accountManager.createAccount(u, p);
                    System.out.println(ok ? "Account created! Wait for superadmin approval." : "Username taken.");
                    pauseAndClear(sc);
                }
                case 2 -> {
                    System.out.print("Username: ");
                    String u = sc.next();
                    System.out.print("Password: ");
                    String p = sc.next();
                    boolean ok = accountManager.login(u, p);
                    if (ok) {
                        Account current = accountManager.getAccount(u);
                        System.out.println("Logged in as " + current.getType());
                        pauseAndClear(sc);
                        current.showMenu(sc, store); // polymorphism — the right menu runs automatically
                    } else {
                        System.out.println("Wrong credentials.");
                        pauseAndClear(sc);
                    }
                }
                case 0 -> System.out.println("Bye!");
                default -> {
                    System.out.println("Invalid choice.");
                    pauseAndClear(sc);
                }
            }
        } while (choice != 0);
    }
}
