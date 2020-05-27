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
@Path("prodotti")
public class Prodotti {

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
    public Prodotti() {
        init();
    }

    /**
     * @author Tosetti_Luca
     *
     * Visualizza i dati relativi ai prodotti memorizzati nel database
     * permettendo di filtrare i risultati ottenuti attraverso vari parametri di
     * query.
     *
     * @param genere Parametro query che permette di specificare il genere dei
     * prodotti che si vogliono visualizzare
     * @param etichetta Parametro query che permette di specificare l'etichetta
     * dei prodotti che si vogliono visualizzare
     * @param costo Parametro query che permette di specificare il costo dei
     * prodotti che si vogliono visualizzare
     * @param nome Parametro query che permette di specificare il nome dei
     * prodotti che si vogliono visualizzare
     * @param marca Parametro query che permette di specificare la marca dei
     * prodotti che si vogliono visualizzare
     * @param descrizione Parametro query che permette di specificare la
     * descrizione dei prodotti che si vogliono visualizzare
     * @return Output XML contenente le informazioni relative a uno o più
     * prodotti / Output messaggio di errore
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    @Path("prodotto")
    public String getProdotto(@QueryParam("genere") String genere, @QueryParam("etichetta") String etichetta, @QueryParam("costo") double costo, @QueryParam("nome") String nome, @QueryParam("marca") String marca, @QueryParam("descrizione") String descrizione) {
        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>500</errorMessage>";
        }

        try {
            String sql = "SELECT * FROM prodotti WHERE";
            if (genere != null) {
                sql = sql + " genere='" + genere + "' AND";
            }
            if (etichetta != null) {
                sql = sql + " etichetta='" + etichetta + "' AND";
            }
            if (costo != 0.00) {
                sql = sql + " costo='" + costo + "' AND";
            }
            if (nome != null) {
                sql = sql + " nome='" + nome + "' AND";
            }
            if (marca != null) {
                sql = sql + " marca='" + marca + "' AND";
            }
            if (descrizione != null) {
                sql = sql + " descrizione='" + descrizione + "' AND";
            }

            sql = sql + " 1";
            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> prd = new ArrayList<Prodotto>();
            while (result.next()) {
                int prdID = result.getInt(1);
                String prdGenere = result.getString(2);
                String prdEtichetta = result.getString(3);
                double prdCosto = result.getDouble(4);
                String prdNome = result.getString(5);
                String prdMarca = result.getString(6);
                String prdDescrizione = result.getString(7);

                prd.add(new Prodotto(prdID, prdGenere, prdEtichetta, prdCosto, prdNome, prdMarca, prdDescrizione));

            }

            if (prd.size() > 0) {
                result.close();
                statement.close();

                output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                output = output + "<return>\n";

                for (int i = 0; i < prd.size(); i++) {
                    output = output + "<prodotto>\n";
                    output = output + "<idProdotto>" + prd.get(i).getIdProdotto() + "</idProdotto>\n";
                    output = output + "<genere>" + prd.get(i).getGenere() + "</genere>\n";
                    output = output + "<etichetta>" + prd.get(i).getEtichetta() + "</etichetta>\n";
                    output = output + "<costo>" + prd.get(i).getCosto() + "</costo>\n";
                    output = output + "<nome>" + prd.get(i).getNome() + "</nome>\n";
                    output = output + "<marca>" + prd.get(i).getMarca() + "</marca>\n";
                    output = output + "<descrizione>" + prd.get(i).getDescrizione() + "</descrizione>\n";
                    output = output + "</prodotto>\n";
                }

                output = output + "</return>\n";
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

    /**
     * @author Tosetti_Luca
     *
     * Consente l'inserimento di nuovi prodotti all'interno del database
     *
     * @param content Body della richiesta POST https/https contenente il/i
     * nuovo/i prodotto/i da dover memorizzare sottoforma di XML
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("prodotto")
    public String postProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotti = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>500</errorMessage>";
            }

            try {
                String sql = "INSERT INTO prodotti (genere,etichetta,costo,nome,marca,descrizione) VALUES ('" + prodotti.get(0).getGenere() + "','" + prodotti.get(0).getEtichetta() + "','" + prodotti.get(0).getCosto() + "','" + prodotti.get(0).getNome() + "','" + prodotti.get(0).getMarca() + "','" + prodotti.get(0).getDescrizione() + "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * @author Tosetti_Luca
     *
     * Consente la modifica di un di un determinato prodotto andando a
     * specificarne l'ID tramite il percorso
     * @param content Body della richiesta PUT http/https contenente i nuovi
     * valori degli attributi del prodotto specificato nel percorso sottoforma
     * di XML
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("prodotto/{idProdotto}")
    public String putProdotto(@PathParam("idProdotto") String idProdotto, String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("prodotti.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Prodotto> prodotto = (ArrayList<Prodotto>) myParse.parseProdotto("prodotti.xml");
            if (!connected) {
                return "<errorMessage>500</errorMessage>";
            }

            if (prodotto.get(0).getGenere() == null || prodotto.get(0).getEtichetta() == null || prodotto.get(0).getNome() == null || prodotto.get(0).getMarca() == null || prodotto.get(0).getCosto() == 0.00 || prodotto.get(0).getDescrizione() == null) {
                return "<errorMessage>406</errorMessage>";
            }
            if (prodotto.get(0).getGenere().isEmpty() || prodotto.get(0).getEtichetta().isEmpty() || prodotto.get(0).getNome().isEmpty() || prodotto.get(0).getMarca().isEmpty() || prodotto.get(0).getDescrizione().isEmpty()) {
                return "<errorMessage>406</errorMessage>";
            }

            try {
                String sql = "UPDATE prodotti SET nome='" + prodotto.get(0).getNome() + "', genere='" + prodotto.get(0).getGenere() + "', etichetta='" + prodotto.get(0).getEtichetta() + "', costo='" + prodotto.get(0).getCosto() + "', nome='" + prodotto.get(0).getNome() + "', marca='" + prodotto.get(0).getMarca() + "', descrizione='" + prodotto.get(0).getDescrizione() + "' WHERE idProdotto='" + idProdotto + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update avvenuto correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Prodotti.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }

    /**
     * @author Tosetti_Luca
     *
     * Consente di eliminare un prodotto andando a specificarne l'ID tramite il
     * percorso
     * @param id ID del prodotto da eliminare
     * @return Output messaggio di successo / Output messaggio di errore
     */
    @DELETE
    @Path("prodotto/{idProdotto}")
    public String deleteProdotto(@PathParam("idProdotto") int id) {
        init();

        if (!connected) {
            return "<errorMessage>500</errorMessage>";
        }

        if (id != 0) {
            try {
                String sql = "DELETE FROM prodotti WHERE idProdotto='" + id + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>500</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Eliminazione avvenuta correttamente</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } else {
            return "<errorMessage>406</errorMessage>";
        }

    }

}
