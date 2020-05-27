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
@Path("utenti")
public class Utenti {

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
    public Utenti() {
        init();
    }

    /*@GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("prova")
    public Response getMessage() {
        Response r = Response.ok("test with GET")
                .build();
        return r;
    }*/
    /**
     * Galimberti Francesco
     *
     * http://localhost:8080/spesa/utenti
     *
     * Visualizza i dati relativi agli utenti memorizzati nel database
     * permettendo di filtrare i risultati ottenuti attraverso vari parametri di
     * query.
     *
     * @param username Parametro query che permette di specificare l'username
     * dell'utente che si vuole visualizzare, con esso gli altri filtri non sono
     * usati
     * @param nome Parametro query che permette di specificare il nome dei
     * utenti che si vogliono visualizzare
     * @param cognome Parametro query che permette di specificare il cognome dei
     * utenti che si vogliono visualizzare
     * @param regione Parametro query che permette di specificare il regione dei
     * utenti che si vogliono visualizzare
     * @return Risposta, con informazioni richieste o messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getUtenti(
            @QueryParam("nome") String nome,
            @QueryParam("cognome") String cognome,
            @QueryParam("regione") String regione) {

        init();
        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;

        } else {

            try {

                String sql = "";
                /*if (username != null) {
                    sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE username='" + username + "';";

                } else {*/
                sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE";

                if (nome != null) {
                    sql += " nome='" + nome + "' AND";
                }

                if (cognome != null) {
                    sql += " cognome='" + cognome + "' AND";
                }

                if (regione != null) {
                    sql += " regione='" + regione + "' AND";
                }

                sql = sql + " 1";
                //}

                // ricerca nominativo nel database
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                ArrayList<Utente> utentiList = new ArrayList<Utente>(0);
                while (result.next()) {
                    String idUtente = result.getString("idUtente");
                    String Username = result.getString("username");
                    String Nome = result.getString("nome");
                    String Cognome = result.getString("cognome");
                    String CodiceFiscale = result.getString("codiceFiscale");
                    String Regione = result.getString("regione");
                    String Via = result.getString("via");
                    String nCivico = result.getString("nCivico");
                    Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);
                    utentiList.add(u);
                }
                result.close();
                statement.close();

                if (utentiList.size() > 0) {
                    output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                    output += "<utenti>";

                    for (int i = 0; i < utentiList.size(); i++) {
                        Utente u = utentiList.get(i);
                        output += "<utente>";
                        output += "<idUtente>" + u.getIdUtente() + "</idUtente>";

                        /*if (username != null) {
                            output += "<nome>" + u.getNome() + "</nome>";
                            output += "<cognome>" + u.getCognome() + "</cognome>";
                            output += "<regione>" + u.getRegione() + "</regione>";

                        } else {*/
                        output += "<username>" + u.getUsername() + "</username>";

                        if (nome == null) {
                            output += "<nome>" + u.getNome() + "</nome>";
                        }
                        if (cognome == null) {
                            output += "<cognome>" + u.getCognome() + "</cognome>";
                        }
                        if (regione == null) {
                            output += "<regione>" + u.getRegione() + "</regione>";
                        }
                        //}

                        output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                        output += "<via>" + u.getVia() + "</via>";
                        output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                        output += "</utente>";
                    }
                    output += "</utenti>";
                    utentiList = new ArrayList<Utente>(0);

                    destroy();
                    r = Response.ok(output).build();
                    return r;

                } else {
                    destroy();
                    r = Response.status(404).entity("<messaggio>Utente non trovato</messaggio>").build();
                    return r;
                }

            } catch (SQLException ex) {
                destroy();
                r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();
                return r;
            }
        }
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("utente")
    public Response getUtente(
            @QueryParam("id") int id,
            @QueryParam("username") String username) {

        String output = "";
        Response r;

        // verifica stato connessione a DBMS
        if (!connected) {

            r = Response.serverError().entity("<messaggio>DBMS Error, impossibile connettersi</messaggio>").build();
            return r;

        } else {
            String sql = "SELECT idUtente, username, nome, cognome, codiceFiscale, regione, via, nCivico FROM utenti WHERE ";

            if (username != null && !username.isEmpty()) {
                sql += "username='" + username + "';";
            } else if (id > 0) {
                sql += "idUtente=" + id + ";";
            } else {
                destroy();
                r = Response.status(402).entity("<messaggio>Parametro non valido o mancante</messaggio>").build();
                return r;
            }

            try {
                Statement statement = spesaDatabase.createStatement();
                ResultSet result = statement.executeQuery(sql);

                result.next();

                String idUtente = result.getString("idUtente");
                String Username = result.getString("username");
                String Nome = result.getString("nome");
                String Cognome = result.getString("cognome");
                String CodiceFiscale = result.getString("codiceFiscale");
                String Regione = result.getString("regione");
                String Via = result.getString("via");
                String nCivico = result.getString("nCivico");

                Utente u = new Utente(idUtente, Username, Nome, Cognome, CodiceFiscale, Regione, Via, nCivico);

                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                output += "<utente>";

                if (username != null) {
                    output += "<idUtente>" + u.getIdUtente() + "</idUtente>";
                } else if (id != 0) {
                    output += "<username>" + u.getUsername() + "</username>";
                }

                output += "<nome>" + u.getNome() + "</nome>";
                output += "<cognome>" + u.getCognome() + "</cognome>";
                output += "<regione>" + u.getRegione() + "</regione>";
                output += "<codiceFiscale>" + u.getCodiceFiscale() + "</codiceFiscale>";
                output += "<via>" + u.getVia() + "</via>";
                output += "<nCivico>" + u.getnCivico() + "</nCivico>";
                output += "</utente>";

                destroy();
                r = Response.ok(output).build();
                return r;

            } catch (SQLException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                destroy();
                r = Response.status(404).entity("<messaggio>Utente non trovato</messaggio>").build();
                return r;
            }

        }
    }
        /**
         * Galimberti Francesco
         *
         * PUT spesa/utenti/1
         *
         * body examples
         * <utente>
         * <username>fraGali</username>
         * <nome>Francesco</nome>
         * <cognome>Galimberti</cognome>
         * <codiceFiscale>GLMFNC01A02B729Q</codiceFiscale>
         * <regione>Lombardia</regione>
         * <via>Giacomo Leopardi</via>
         * <nCivico>5</nCivico>
         * </utente>
         *
         * Consente la modifica di un utente andando a specificarne l'ID tramite
         * il percorso
         *
         * @param idUtente identificativo dell'utente da modificare
         * @param content Body della richiesta PUT http/https contenente i nuovi
         * valori degli attributi dell'utente specificato nel percorso
         * (sottoforma di XML)
         * @return Risposta, con messaggio e stato
         */
        @PUT
        @Path("utente/{idUtente}")
        @Consumes({MediaType.TEXT_PLAIN, MediaType.TEXT_XML})
        public Response putUtente
        (
            @PathParam("idUtente")
        int idUtente,
                String content
        
            ) {
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
                    file = new BufferedWriter(new FileWriter("utente.xml"));
                    file.write(content);
                    file.flush();
                    file.close();

                    myParse = new MyParser();
                    Utente u = myParse.parseFileUtente("utente.xml");

                    if (idUtente != 0) {
                        /*if (u.getNome() == null || u.getCognome() == null || u.getCognome() == null || u.getCodiceFiscale() == null || u.getRegione() == null || u.getnCivico() == null || u.getVia() == null || u.getUsername() == null) {
                        r = Response.status(409).entity("Error, Malformed XML Body").build();
                        return r;
                    }
                    if (u.getNome().isEmpty() || u.getCognome().isEmpty() || u.getCognome().isEmpty() || u.getCodiceFiscale().isEmpty() || u.getRegione().isEmpty() || u.getnCivico().isEmpty() || u.getVia().isEmpty() || u.getUsername().isEmpty()) {
                        r = Response.status(409).entity("Error, Malformed XML Body").build();
                        return r;
                    }*/

                        Statement statement = spesaDatabase.createStatement();

                        StringBuilder columns = new StringBuilder(255);
                        if (u.getUsername() != null && !u.getUsername().isEmpty()) {
                            //sql += " username='" + u.getUsername() + "', ";
                            columns.append("username='").append(u.getUsername()).append("'");
                        }
                        if (u.getNome() != null && !u.getNome().isEmpty()) {
                            //sql += " nome='" + u.getNome() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("nome='").append(u.getNome()).append("'");
                        }
                        if (u.getCognome() != null && !u.getCognome().isEmpty()) {
                            //sql += " cognome='" + u.getCognome() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("cognome='").append(u.getCognome()).append("'");
                        }
                        if (u.getCodiceFiscale() != null && !u.getCodiceFiscale().isEmpty()) {
                            //sql += " codiceFiscale='" + u.getCodiceFiscale() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("codiceFiscale='").append(u.getCodiceFiscale()).append("'");
                        }
                        if (u.getRegione() != null && !u.getRegione().isEmpty()) {
                            //sql += " regione='" + u.getRegione() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("regione='").append(u.getRegione()).append("'");
                        }
                        if (u.getVia() != null && !u.getVia().isEmpty()) {
                            //sql += " via='" + u.getVia() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("via='").append(u.getVia()).append("'");
                        }
                        if (u.getnCivico() != null && !u.getnCivico().isEmpty()) {
                            //sql += " nCivico='" + u.getnCivico() + "', ";
                            if (columns.length() > 0) {
                                columns.append(", ");
                            }
                            columns.append("nCivico='").append(u.getnCivico()).append("'");
                        }

                        if (columns.length() > 0) {
                            String sql = "UPDATE utenti SET " + columns.toString()
                                    + " WHERE idUtente = " + idUtente + ";";
                            if (statement.executeUpdate(sql) <= 0) {
                                statement.close();
                                r = Response.serverError().entity("<messaggio>DBMS SQL Error, impossibile modificare utenti</messaggio>").build();
                                return r;
                            }
                            statement.close();
                            destroy();
                            r = Response.ok("<messaggio>Update avvenuto correttamente</messaggio>").build();
                            return r;

                        } else {
                            r = Response.status(404).entity("<messaggio>parametri non validi</messaggio>").build();
                            return r;
                        }

                    } else {
                        r = Response.status(403).entity("<messaggio>idUtente non valido</messaggio>").build();
                        return r;
                    }

                } catch (IOException ex) {
                    Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    r = Response.serverError().entity("<messaggio>DBMS IO Error</messaggio>").build();

                } catch (SQLException ex) {
                    Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    r = Response.serverError().entity("<messaggio>DBMS SQL Error</messaggio>").build();

                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    r = Response.status(409).entity("<messaggio>Error, Malformed XML Body</messaggio>").build();

                } catch (SAXException ex) {
                    Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
                    destroy();
                    r = Response.serverError().entity("<messaggio>DBMS SAXE Error</messaggio>").build();
                }
                return r;
            }
        }

        /**
         * SPANGARO FRANCESCO inserisce i dati di un utente fornito nel body in
         * formato XML come definito nella progettazione api
         *
         * @param content sono i dati inviati dall'utilizzatore, salvato nella
         * cartella server xampp/tomcat/bin/utente.xml
         * @return varie tipologie di ritorno, conferma se corretto, altrimenti
         * messaggi di errore corrispondenti
         */
        @POST
        @Path("utenteXML")
        @Consumes(MediaType.TEXT_XML)
        public String postUtenteXML
        (String content
        
            ) {
        init();
            try {
                String xsdFile = "\\xml\\utente.xsd";
                BufferedWriter writer;
                writer = new BufferedWriter(new FileWriter("utente.xml"));
                writer.write(content);
                writer.flush();
                writer.close();
                Utente utente = new Utente();

                /*try {
                MyValidator.validate("entry.xml", xsdFile);
            } catch (SAXException ex) {
                Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
                return "<errorMessage>400 Malformed XML</errorMessage>";
            }*/
                MyParser parse = new MyParser();
                utente = parse.parseUtente("utente.xml");
                if (!connected) {
                    return "<errorMessage>400</errorMessage>";
                }
                String sql = "INSERT INTO utenti(username, nome, cognome, password, codiceFiscale, regione, via, nCivico) VALUES('" + utente.getUsername() + "', '" + utente.getNome() + "', '" + utente.getCognome() + "', '" + utente.getPassword() + "', '" + utente.getCodiceFiscale() + "', '" + utente.getRegione() + "', '" + utente.getVia() + "', '" + utente.getnCivico() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";

            } catch (IOException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "<errorMessage>400</errorMessage>";
        }

        /**
         * SPANGARO FRANCESCO inserisce i dati di un utente fornito nel body in
         * formato JSON come definito nella progettazione api
         *
         * @param content sono i dati inviati dall'utilizzatore, parsati dal
         * metodo (libreria usata: json-20190722.jar)
         * @return varie tipologie di ritorno, conferma se corretto, altrimenti
         * messaggi di errore corrispondenti
         */
        @POST
        @Path("utenteJSON")
        @Consumes(MediaType.APPLICATION_JSON)
        public String postUtenteJSON
        (String content
        
            ) {
        init();
            try {
                JSONObject obj = new JSONObject(content);
                Utente utente = new Utente();
                utente.setUsername(obj.getJSONObject("utente").getString("username"));
                utente.setNome(obj.getJSONObject("utente").getString("nome"));
                utente.setCognome(obj.getJSONObject("utente").getString("cognome"));
                utente.setPassword(obj.getJSONObject("utente").getString("password"));
                utente.setCodiceFiscale(obj.getJSONObject("utente").getString("codiceFiscale"));
                utente.setRegione(obj.getJSONObject("utente").getString("regione"));
                utente.setVia(obj.getJSONObject("utente").getString("via"));
                utente.setnCivico(obj.getJSONObject("utente").getString("nCivico"));

                if (!connected) {
                    return "<errorMessage>400</errorMessage>";
                }
                String sql = "INSERT INTO utenti(username, nome, cognome, password, codiceFiscale, regione, via, nCivico) VALUES('" + utente.getUsername() + "', '" + utente.getNome() + "', '" + utente.getCognome() + "', '" + utente.getPassword() + "', '" + utente.getCodiceFiscale() + "', '" + utente.getRegione() + "', '" + utente.getVia() + "', '" + utente.getnCivico() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";

            } catch (SQLException ex) {
                Logger.getLogger(Utenti.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "<errorMessage>400</errorMessage>";
        }

    }
