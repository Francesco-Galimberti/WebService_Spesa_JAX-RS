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
@Path("risposte")
public class Risposte {

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
    public Risposte() {
        init();
    }



    /**
     * Galimberti Francesco
     *
     * POST spesa/risposte
     *
     * <risposta>
     * <idUtente>1</idUtente>
     * <idRichiesta>2</idRichiesta>
     * </risposta>
     *
     * Consente l'inserimento di una nuova risposta ad una richiesta di spesa
     * all'interno del database
     *
     * @param content Body della richiesta POST https/https contenente il nuovo
     * prodotto da dover memorizzare sottoforma di XML
     * @return Risposta, con messaggio e stato
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("risposta")
    public Response postRisposta(String content) {

        // verifica stato connessione a DBMS
        init();
        MyParser myParse;
        Response r;

        if (!connected) {
             r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;
        } else {

            try {
                BufferedWriter file;
                file = new BufferedWriter(new FileWriter("risposta.xml"));
                file.write(content);
                file.flush();
                file.close();

                myParse = new MyParser();
                Risposta rr = myParse.parseFileRisposta("risposta.xml");

                if (rr.getIdUtente() == null || rr.getIdRichiesta() == null) {
                    r = Response.status(402).entity("<messaggio>Parametri non validi<messaggio>").build();
                    return r;
                }
                if (rr.getIdUtente().isEmpty() || rr.getIdRichiesta().isEmpty()) {
                    r = Response.status(402).entity("<messaggio>Parametri non validi<messaggio>").build();
                    return r;
                }

                // aggiunta voce nel database
                Statement statement = spesaDatabase.createStatement();
                String sql = "INSERT risposte(rifUtente, rifRichiesta) VALUES(" + rr.getIdUtente() + ", " + rr.getIdRichiesta() + ");";

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    r = Response.status(404).entity("<messaggio>DBMS SQL Error, impossibile effettuare inserimento</messaggio>").build();
                    return r;
                }

                statement.close();
                destroy();
                r = Response.ok("<messaggio>Inserimento avvenuto correttamente</messaggio>").build();
                return r;

            } catch (IOException ex) {
                Logger.getLogger(Risposte.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS IO Error</messaggio>").build();

            } catch (SQLException ex) {
                Logger.getLogger(Risposte.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();

            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Risposte.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(409).entity("<messaggio>Error, Malformed XML Body</messaggio>").build();

            } catch (SAXException ex) {
                Logger.getLogger(Risposte.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SAXE Error</messaggio>").build();
            }
            return r;

        }
    }

    /**
     * @author Tosetti_Luca
     *
     * Visualizza i dati relativi alle richieste memorizzate nel database oppure
     * di uno specifico utente andando a specificare l'id di tale utente come
     * parametro query
     * @param id ID dell'utente del quale si vogliono ottenere le varie
     * richieste
     * @return Output XML contenente le informazioni relative a una o più
     * richieste / Output messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("risposte")
    public String getRisposte(@QueryParam("idUtente") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>500</errorMessage>";
        }

        try {
            String sql = "SELECT idRisposta,rifRichiesta,rifUtente FROM risposte WHERE";
            if (id != null && !id.isEmpty()) {
                sql = sql + " rifUtente='" + id + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Risposta> risp = new ArrayList<Risposta>();
            while (result.next()) {
                String rispID = result.getString(1);
                String rispRifRichiesta = result.getString(2);
                String rispRifUtente = result.getString(3);
                risp.add(new Risposta(rispRifUtente, rispRifRichiesta, rispID));

            }

            if (risp.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<elencoRisposte>\n";

                for (int i = 0; i < risp.size(); i++) {
                    output = output + "<risposta>\n";
                    output = output + "<rifUtente>" + risp.get(i).getIdUtente() + "</rifUtente>\n";
                    output = output + "<idRisposta>" + risp.get(i).getIdRisposta() + "</idRisposta>\n";
                    output = output + "<rifRichiesta>" + risp.get(i).getIdRichiesta() + "</rifRichiesta>\n";
                    output = output + "</risposta>\n";
                }

                output = output + "</elencoRisposte>\n";
            } else {
                result.close();
                statement.close();
                destroy();
                return output;
            }
        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }
}
