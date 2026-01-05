import java.sql.*;
import java.util.Scanner;

public class GestScuolaCalcio {

    // Costanti di configurazione del Database
    private static final String DB_URL = "jdbc:mariadb://localhost:3306/scuolacalcio";
    private static final String DB_USER = "XX";
    private static final String DB_PASSWORD = "XX";

    public static void main(String args[]) {
        // Connessione al DB
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            //altro user: allenatore, password)
            System.out.println("Connessione OK \n");

            Scanner scanner = new Scanner(System.in);
            int cmd;

            do {
                operazioni();
                cmd = scanner.nextInt();
                scanner.nextLine();
                switch (cmd) {
                    case 1:
                        inserisciTesserato(con, scanner);
                        break;
                    case 2:
                        eliminaTesserato(con, scanner);
                        break;
                    case 3:
                        visualizzaAtleti(con);
                        break;
                    case 4:
                        visualizzaStaff(con);
                        break;
                    case 5:
                        registraVisitaMedica(con, scanner);
                        break;
                    case 6:
                        reportVisiteMedicheScadute(con);
                        break;
                    case 7:
                        reportVisiteMedicheAtleta(con, scanner);
                        break;
                    case 0:
                        System.out.println("Arrivederci");
                        break;
                    default:
                        System.out.println("Scelta non valida!");
                }
            }while (cmd != 0);

          //Quando esco dal programma chiudo connessione e scanner
            con.close();
            scanner.close();

        } catch (Exception e) { //in caso di mancata connessione
            System.out.println("Connessione Fallita\n");
            System.out.println(e);
        }
    }

    private static void operazioni() {
        System.out.println("\n1 - Inserisci un nuovo tesserato");
        System.out.println("2 - Elimina un tesserato");
        System.out.println("3 - Visualizza tutti gli atleti");
        System.out.println("4 - Visualizza allenatori e dirigenti");
        System.out.println("5 - Registra una visita medica");
        System.out.println("6 - Report atleti con visita medica scaduta o in scadenza");
        System.out.println("7 - Report visite mediche di un atleta");
        System.out.println("0 - Esci dal programma");
        System.out.print("Seleziona un'opzione: ");
    }

    //METODI SQL
    private static void inserisciTesserato(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Codice Fiscale: ");
        String cf = scanner.nextLine();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Data di nascita (YYYY-MM-DD): ");
        String dataNascita = scanner.nextLine();
        System.out.print("Seleziona ruolo\n1 - Atleta\n2 - Dirigente\n3 - Allenatore\n4 - Presidente\nRuolo: ");
        String ruolo = "";
        do {
            switch (scanner.nextInt()) {
                case 1: ruolo = "ATLETA"; break;
                case 2: ruolo = "DIRIGENTE"; break;
                case 3: ruolo = "ALLENATORE"; break;
                case 4: ruolo = "PRESIDENTE"; break;
                default: System.out.println("Input non valido");
            }
            scanner.nextLine();
        } while (ruolo.isEmpty());

        String sqlTesserato = "INSERT INTO TESSERATO (CF, COGNOME, NOME, DATANASCITA) VALUES (?, ?, ?, ?)";
        String sqlRuolo = "INSERT INTO " + ruolo + " (CF) VALUES (?)";

        PreparedStatement psTesserato = con.prepareStatement(sqlTesserato);
        psTesserato.setString(1, cf);
        psTesserato.setString(2, cognome);
        psTesserato.setString(3, nome);
        psTesserato.setString(4, dataNascita);
        int rows = psTesserato.executeUpdate();
        psTesserato.close(); //chiudo il prepared statement

       if (rows > 0) {//se non ho problemi, inserisco anche il ruolo
           PreparedStatement psRuolo = con.prepareStatement(sqlRuolo);
           psRuolo.setString(1, cf);
           psRuolo.executeUpdate();
           psRuolo.close(); //chiudo il prepared statement
           System.out.println(cognome + " " + nome + " inserito come " + ruolo);
       } else {
            System.out.println("Errore nell'inserimento del tesserato.");
        }
    }

    private static void eliminaTesserato(Connection con, Scanner scanner) throws SQLException {
        System.out.print("CF tesserato da eliminare: ");
        String cf = scanner.nextLine();
        String sql = "DELETE FROM Tesserato WHERE CF = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cf);
        int rows = ps.executeUpdate();
        ps.close();
        if (rows > 0) {
            System.out.println("Tesserato eliminato con successo.");
        } else {
            System.out.println("Nessun tesserato trovato.");
        }
    }

    private static void visualizzaAtleti(Connection con) throws SQLException {
        String sql = "SELECT T.CF, T.Cognome, T.Nome, T.DataNascita " +
                "FROM Tesserato T JOIN Atleta A ON T.CF = A.CF " +
                "ORDER BY T.Cognome ASC, T.Nome ASC, T.DataNascita ASC";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        int count = 0;
        while (rs.next()) {
            System.out.println(rs.getString("CF") + " - " + rs.getString("Cognome") + " " +
                    rs.getString("Nome") + " - " + rs.getDate("DataNascita"));
            count++;
        }
        System.out.println("\nAtleti tesserati: " + count);

        rs.close(); //si chiude in ordine inverso all'apertura
        ps.close();
    }

    private static void visualizzaStaff(Connection con) throws SQLException {
        String sqlAllenatori = "SELECT T.CF, T.Cognome, T.Nome, T.DataNascita " +
                "FROM Tesserato T JOIN Allenatore A ON T.CF = A.CF " +
                "ORDER BY T.Cognome ASC, T.Nome ASC, T.DataNascita ASC";
        PreparedStatement ps = con.prepareStatement(sqlAllenatori);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nAllenatori:");
        while (rs.next()) {
            System.out.println(rs.getString("CF") + " - " + rs.getString("Cognome") + " " +
                    rs.getString("Nome") + " - " + rs.getDate("DataNascita"));
        }
        rs.close();
        ps.close();

        String sqlDirigenti = "SELECT T.CF, T.Cognome, T.Nome, T.DataNascita " +
                "FROM Tesserato T JOIN Dirigente D ON T.CF = D.CF " +
                "ORDER BY T.Cognome ASC, T.Nome ASC, T.DataNascita ASC";
        ps = con.prepareStatement(sqlDirigenti);
        rs = ps.executeQuery();

        System.out.println("\nDirigenti:");
        while (rs.next()) {
            System.out.println(rs.getString("CF") + " - " + rs.getString("Cognome") + " " +
                    rs.getString("Nome") + " - " + rs.getDate("DataNascita"));
        }
        rs.close();
        ps.close();

        String sqlPresidente = "SELECT T.CF, T.Cognome, T.Nome, T.DataNascita " +
                "FROM Tesserato T JOIN Presidente P ON T.CF = P.CF " +
                "ORDER BY T.Cognome ASC, T.Nome ASC, T.DataNascita ASC";
        ps = con.prepareStatement(sqlPresidente);
        rs = ps.executeQuery();

        System.out.println("\nPresidente:");
        while (rs.next()) {
            System.out.println(rs.getString("CF") + " - " + rs.getString("Cognome") + " " +
                    rs.getString("Nome") + " - " + rs.getDate("DataNascita"));
        }

        rs.close();
        ps.close();
    }

    private static void registraVisitaMedica(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Codice Fiscale: ");
        String cf = scanner.nextLine();
        System.out.print("Data Visita YYYY-MM-DD: ");
        String data = scanner.nextLine();
        System.out.print("Data Scadenza YYYY-MM-DD: ");
        String dataScadenza = scanner.nextLine();
        System.out.print("Note (Premi invio se non ci sono): ");
        String note = scanner.nextLine();
        if (note.isEmpty()) {
            note = "";
        }

        String sql = "INSERT INTO VisitaMedica (CF_Tesserato, ID, data, idoneoFinoAl, note) VALUES (?, (SELECT COALESCE(MAX(ID), 0) + 1 FROM VisitaMedica AS temp WHERE CF_Tesserato = ?), ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cf);
        ps.setString(2, cf);
        ps.setString(3, data);
        ps.setString(4, dataScadenza);
        ps.setString(5, note);

        int rows = ps.executeUpdate();
        ps.close();

        if (rows > 0) {
            // Se la visita è inserita correttamente posso aggiornare il campo ridondante
            String sqlUpdate = "UPDATE Atleta SET idoneoFinoAl = ? WHERE CF = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setString(1, dataScadenza);
            psUpdate.setString(2, cf);
            psUpdate.executeUpdate();
            psUpdate.close();
            System.out.println("Visita medica registrata correttamente.");
        } else {
            System.out.println("Impossibile inserire il record");
        }
    }


    private static void reportVisiteMedicheScadute(Connection con) throws SQLException {
        String sql = "SELECT T.CF, T.cognome, T.nome, A.idoneoFinoAl, VM.note " +
                "FROM Atleta AS A " +
                "JOIN Tesserato AS T ON A.CF = T.CF " +
                "LEFT JOIN VisitaMedica AS VM ON A.CF = VM.CF_Tesserato AND VM.idoneoFinoAl = A.idoneoFinoAl " +
                "WHERE A.idoneoFinoAl IS NULL " +
                "   OR A.idoneoFinoAl < CURDATE() " + // Scaduta
                "   OR (A.idoneoFinoAl BETWEEN CURDATE() AND CURDATE() + INTERVAL 1 MONTH) " + // In scadenza
                "ORDER BY T.cognome ASC, T.nome ASC";

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString("CF") + " - " +
                    rs.getString("Cognome") + " " +
                    rs.getString("Nome") + " - Scadenza: " +
                    rs.getDate("idoneoFinoAl") + " - " +
                    rs.getString("note"));
        }
        rs.close();
        ps.close();
    }


    private static void reportVisiteMedicheAtleta(Connection con, Scanner scanner) throws SQLException {
        System.out.print("Inserisci il Codice Fiscale dell'atleta: ");
        String cf = scanner.nextLine();

        String sql = "SELECT ID, data, idoneoFinoAl, note FROM VisitaMedica " +
                "WHERE CF_Tesserato = ? " +
                "ORDER BY idoneoFinoAl ASC";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cf);
        ResultSet rs = ps.executeQuery();

        System.out.println("\nVisite Mediche per CF: " + cf);
        while (rs.next()) {
            System.out.println(rs.getInt("ID") + " Data Visita: " +
                    rs.getDate("data") + " - Scadenza: " +
                    rs.getDate("idoneoFinoAl") + " - Note: " +
                    rs.getString("note"));
        }
        rs.close();
        ps.close();
    }
}
