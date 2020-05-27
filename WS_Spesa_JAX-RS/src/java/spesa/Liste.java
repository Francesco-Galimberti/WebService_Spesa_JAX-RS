/**
 * TOSETTI LUCA
 *
 * @GET
 * http://localhost:8080/spesa/risposte
 * http://localhost:8080/spesa/prodotto?genere={genere}&nome={nome}...
 * @POST http://localhost:8080/spesa/prodotto
 * @PUT http://localhost:8080/spesa/prodotto/{idProdotto}
 * @DELETE http://localhost:8080/spesa/prodotto/{idProdotto}
 */
/**
 * SPANGARO FRANCESCO
 *
 * @GET
 * http://localhost:8080/spesa/richiestaXML/{id}
 * http://localhost:8080/spesa/richiestaJSON/{id}
 * @POST http://localhost:8080/spesa/utenteXML
 * http://localhost:8080/spesa/utenteJSON
 * http://localhost:8080/spesa/richiestaXML
 * http://localhost:8080/spesa/richiestaJSON
 * @DELETE http://localhost:8080/spesa/lista?id={rifRichiesta}
 */
/**
 * GALIMBERTI FRANCESCO
 *
 * @GET
 * http://localhost:8080/spesa/utenti
 * http://localhost:8080/spesa/utenti?username={username}&nome={nome}...
 * @POST http://localhost:8080/spesa/risposta
 * @PUT http://localhost:8080/spesa/utenti/{idUtente}
 * @DELETE http://localhost:8080/spesa/richieste/{idRichiesta}/{idUtente}
 */
/**
 * ROVELLI ANDREA
 *
 * @GET
 * http://localhost:8080/spesa/lista?rifRichiesta={id}
 * @POST http://localhost:8080/spesa/lista
 *
 * @PUT http://localhost:8080/spesa/updLista
 */
package spesa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import javax.ws.rs.ext.MessageBodyReader;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * REST Web Service
 *
 * @author Galimberti Francesco
 */
@Path("liste")
public class Liste{

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    // attivazione servlet (connessione a DBMS)
    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesaDatabase = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    // disattivazione servlet (disconnessione da DBMS)
    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of Api
     */
    public Liste() {
        init();
    }

    /**
     * SPANGARO FRANCESCO cancella una lista dal database, una lista è l'insieme
     * dei prodotti, assegnata poi ad una richiesta, lista che corrisponde
     * all'id inserito come parametro della query, riferimento alla richiesta
     * corrispondente
     *
     * @param id è l'id su cui si deve basare per fare la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @DELETE
    @Path("lista")
    public String deleteLista(@QueryParam("id") int rifRichiesta) {
        init();

        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }
        try {
            String sql = "DELETE FROM liste WHERE rifRichiesta='" + rifRichiesta + "'";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Eliminazione avvenuta correttamente</message>";
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
    }

    /**
     * SPANGARO FRANCESCO Metodo per il casting delle stringhe restituite dal
     * database, da String a Time
     */
    public java.sql.Time getTime(String stringa) {
        DateFormat formato = new SimpleDateFormat("HH:mm:ss");
        java.sql.Time ora = null;
        try {
            ora = new java.sql.Time(formato.parse(stringa).getTime());
        } catch (ParseException ex) {
            Logger.getLogger(MyParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ora;
    }

    /**
     * @author Rovelli Andrea
     *
     * GET spesa/lista
     *
     * Consente di ottenere la lista della spesa di un determinato utente
     *
     * @param id Parametro che identifica la lista di un determinato utente
     *
     * @return Risposta, con messaggio e stato
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("lista")
    public String getLista(@QueryParam("rifRichiesta") String id) {

        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT Costo,Nome,Marca FROM prodotti p, liste l WHERE p.idProdotto = l.rifProdotto AND ";
            if (!id.isEmpty()) {
                sql = sql + " l.rifRichiesta='" + id + "'";
            }

            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> spesa = new ArrayList(0);

            while (result.next()) {

                double costo = result.getDouble("costo");
                String marca = result.getString("marca");
                String nome = result.getString("nome");

                Prodotto prodotto = new Prodotto(costo, marca, nome);

                spesa.add(prodotto);
            }
            result.close();
            statement.close();

            output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            output += "<listaSpesa>\n";

            if (!spesa.isEmpty()) {

                for (int i = 0; i < spesa.size(); i++) {
                    Prodotto p = spesa.get(i);

                    output += "<prodotto>\n";
                    output += "<costo>";
                    output += p.getCosto();
                    output += "</costo>\n";
                    output += "<nome>";
                    output += p.getNome();
                    output += "</nome>\n";
                    output += "<marca>";
                    output += p.getMarca();
                    output += "</marca>\n";
                    output += "</prodotto>\n";
                }

                output += "</listaSpesa>\n";
            } else {

                destroy();
                return "<errorMessage>404</errorMessage>";

            }

        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

    /**
     * @author Rovelli Andrea
     *
     * POST spesa/lista
     *
     * Consente di inserire nella lista della spesa un nuovo prodotto
     *
     * @param content Body contenente tutti i valori necessari all'esecuzione
     * della POST come il riferimento alla richiesta, al prodotto e la quantità
     * da comprare
     *
     * @return Risposta, con messaggio e stato
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("lista")
    public String postLista(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("lista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Lista> liste = (ArrayList<Lista>) myParse.parseDocument("lista.xml", "post");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO liste (rifRichiesta, rifProdotto, quantita) VALUES ('" + liste.get(0).getRifRichiesta() + "','" + liste.get(0).getRifProdotto() + "','" + liste.get(0).getQuantita() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento effettuato</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Liste.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Liste.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Liste.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * @author Rovelli Andrea
     *
     * PUT spesa/lista
     *
     * Consente di aggiornare un prodotto all'interno di una lista della spesa
     *
     * @param content Body contenente tutti i valori necessari all'esecuzione
     * della PUT come l'identificativo di un prodotto all'interno di una lista,
     * il riferimento alla richiesta, al prodotto e la quantità da comprare
     *
     * @return Risposta, con messaggio e stato
     *
     * esempio:
     *
     * <liste>
     * <lista>
     * <idLista>7</idLista>
     * <rifRichiesta>2</rifRichiesta>
     * <rifProdotto>1</rifProdotto>
     * <quantita>5</quantita>
     * </lista>
     * </liste>
     */
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("lista")
    public String putLista(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("updLista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Lista> lista = (ArrayList<Lista>) myParse.parseDocument("updLista.xml", "put");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE liste SET rifRichiesta='" + lista.get(0).getRifRichiesta() + "', rifProdotto='" + lista.get(0).getRifProdotto() + "', quantita='" + lista.get(0).getQuantita() + "' WHERE idLista='" + lista.get(0).getIdLista() + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update effettuato</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

}
