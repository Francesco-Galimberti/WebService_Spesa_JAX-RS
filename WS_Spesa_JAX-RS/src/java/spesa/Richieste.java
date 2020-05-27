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
@Path("richieste")
public class Richieste {

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
    public Richieste() {
        init();
    }

    /**
     * SPANGARO FRANCESCO visualizza i dati di una richiesta da id fornito nella
     * path in formato xml come definito nella progettazione api
     *
     * @param id è l'id su cui si baserà la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @GET
    @Path("richiestaXML/{id}")
    @Produces(MediaType.TEXT_XML)
    public String getRichiestaXMLDaId(@PathParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {
                Richiesta richiesta = new Richiesta();
                String sql = "SELECT rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta FROM richieste where idRichiesta ='" + id + "'";
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();
                richiesta.setRifUtente(result.getInt(1));
                richiesta.setOraInizio(result.getString(2));
                richiesta.setOraFine(result.getString(3));
                richiesta.setDurata(result.getString(4));

                result.close();
                statement.close();

                if (richiesta.getRifUtente() != 0) {
                    output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                    output = output + "<richiesta>\n";
                    output = output + "<rifUtente>" + richiesta.getRifUtente() + "</rifUtente>\n";
                    output = output + "<oraInizio>" + richiesta.getOraInizio() + "</oraInizio>\n";
                    output = output + "<oraFine>" + richiesta.getOraFine() + "</oraFine>\n";
                    output = output + "<durata>" + richiesta.getDurata() + "</durata>\n";
                    output = output + "</richiesta>";

                } else {
                    destroy();
                    return "<errorMessage>404</errorMessage>";
                }

            } catch (SQLException ex) {
                Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
            destroy();
            return output;
        }
    }

    /**
     * SPANGARO FRANCESCO visualizza i dati di una richiesta da id fornito nella
     * path in formato JSON come definito nella progettazione api
     *
     * @param id è l'id su cui si baserà la ricerca
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @GET
    @Path("richiestaJSON/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRichiestaJSONDaId(@PathParam("id") String id) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        } else {
            try {
                Richiesta richiesta = new Richiesta();
                String sql = "SELECT rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta FROM richieste where idRichiesta ='" + id + "'";
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();
                richiesta.setRifUtente(result.getInt(1));
                richiesta.setOraInizio(result.getString(2));
                richiesta.setOraFine(result.getString(3));
                richiesta.setDurata(result.getString(4));

                result.close();
                statement.close();

                if (richiesta.getRifUtente() != -1) {
                    output = "{\"richiesta\":{\n";
                    output = output + "\"rifUtente\":\"" + richiesta.getRifUtente() + "\",\n";
                    output = output + "\"oraInizio\":\"" + richiesta.getOraInizio() + "\",\n";
                    output = output + "\"oraFine\":\"" + richiesta.getOraFine() + "\",\n";
                    output = output + "\"durata\":\"" + richiesta.getDurata() + "\"\n";
                    output = output + "}\n}";

                } else {
                    destroy();
                    return "<errorMessage>404</errorMessage>";
                }

            } catch (SQLException ex) {
                Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
            destroy();
            return output;
        }
    }

    /**
     * SPANGARO FRANCESCO inserisce i dati di una richiesta fornita nel body in
     * formato XML come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, salvato nella
     * cartella server xampp/tomcat/bin/richiesta.xml
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @POST
    @Path("richiestaXML")
    @Consumes(MediaType.TEXT_XML)
    public String postRichiestaXML(String content) {
        init();
        try {
            String xsdFile = "\\xml\\richiesta.xsd";
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("richiesta.xml"));
            writer.write(content);
            writer.flush();
            writer.close();
            Richiesta richiesta = new Richiesta();

            /*try {
                MyValidator.validate("entry.xml", xsdFile);
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                return "<errorMessage>400 Malformed XML</errorMessage>";
            }*/
            MyParser parse = new MyParser();
            richiesta = parse.parseRichiesta("richiesta.xml");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO richieste(rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta) VALUES(" + richiesta.getRifUtente() + ", '" + richiesta.getOraInizio() + "', '" + richiesta.getOraFine() + "', '" + richiesta.getDurata() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (IOException ex) {
            Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * SPANGARO FRANCESCO inserisce i dati di una richiesta fornita nel body in
     * formato JSON come definito nella progettazione api
     *
     * @param content sono i dati inviati dall'utilizzatore, parsati dal metodo
     * (libreria usata: json-20190722.jar)
     * @return varie tipologie di ritorno, conferma se corretto, altrimenti
     * messaggi di errore corrispondenti
     */
    @POST
    @Path("richiestaJSON")
    @Consumes(MediaType.APPLICATION_JSON)
    public String postRichiestaJSON(String content) {
        init();
        try {
            JSONObject obj = new JSONObject(content);
            Richiesta richiesta = new Richiesta();
            richiesta.setRifUtente(obj.getJSONObject("richiesta").getInt("rifUtente"));
            richiesta.setOraInizio(obj.getJSONObject("richiesta").getString("oraInizio"));
            richiesta.setOraFine((obj.getJSONObject("richiesta").getString("oraFine")));
            richiesta.setDurata((obj.getJSONObject("richiesta").getString("durata")));

            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }
            String sql = "INSERT INTO richieste(rifUtente, oraInizioConsegna, oraFineConsegna, durataRichiesta) VALUES(" + richiesta.getRifUtente() + ", '" + richiesta.getOraInizio() + "', '" + richiesta.getOraFine() + "', '" + richiesta.getDurata() + "')";
            Statement statement = spesaDatabase.createStatement();

            if (statement.executeUpdate(sql) <= 0) {
                statement.close();
                return "<errorMessage>403</errorMessage>";
            }

            statement.close();
            destroy();
            return "<message>Inserimento avvenuto correttamente</message>";

        } catch (SQLException ex) {
            Logger.getLogger(Richieste.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

}
