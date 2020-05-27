<%-- 
    Document   : index
    Created on : 22-mag-2020, 9.21.11
    Author     : Galimberti Francesco
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
    <center>
        <h1>WS_SPESA (Galimberti, Rovelli, Spangaro, Tosetti)</h1>
        <h3>
            Al tempo del coronavirus, ma anche in tempi normali, si presenta sovente il problema delle persone anziane che vivono da sole e che non possono uscire di casa (perché malate o perchè non vogliono ammalarsi).

            In ogni città però ci sono persone che invece vanno e tornano dal lavoro, da scuola e decidono sulla via del ritorno di passare da un supermercato o da un negozio per fare la spesa.

            Proporre l'analisi della gestione di un sistema di condivisione della spesa in cui da una parte c'è la segnalazione di chi, durante un viaggio di ritorno a casa, è disponibile a fare la spesa anche per chi è obbligato a casa e dall'altra chi invece ha bisogno di qualcosa ma non può uscire.
        </h3>
        

        <p>
            <br>TOSETTI LUCA
            <br>@GET
            <br>http://localhost:8080/spesa/risposte
            <br>http://localhost:8080/spesa/prodotto?genere={genere}&nome={nome}...
            <br>@POST 
            <br>http://localhost:8080/spesa/prodotto
            <br>@PUT 
            <br>http://localhost:8080/spesa/prodotto/{idProdotto}
            <br>@DELETE 
            <br>http://localhost:8080/spesa/prodotto/{idProdotto}
        </p>

        <p>
            <br>SPANGARO FRANCESCO
            <br>@GET
            <br>http://localhost:8080/spesa/richiestaXML/{id}
            <br>http://localhost:8080/spesa/richiestaJSON/{id}
            <br>@POST 
            <br>http://localhost:8080/spesa/utenteXML
            <br>http://localhost:8080/spesa/utenteJSON
            <br>http://localhost:8080/spesa/richiestaXML
            <br>http://localhost:8080/spesa/richiestaJSON
            <br>@DELETE 
            <br>http://localhost:8080/spesa/lista?id={rifRichiesta}
        </p>

        <p>
            <br>GALIMBERTI FRANCESCO
            <br>@GET
            <br>http://localhost:8080/spesa/utenti/utente?id={idUtente}
            <br>http://localhost:8080/spesa/utenti/utente?username={username}
            <br>http://localhost:8080/spesa/utenti
            <br>http://localhost:8080/spesa/utenti?nome={nome}&regione={regione}...
            <br>@POST 
            <br>http://localhost:8080/spesa/risposta
            <br>@PUT
            <br>http://localhost:8080/spesa/utenti/{idUtente}
            <br>@DELETE 
            <br>http://localhost:8080/spesa/richieste/{idRichiesta}/{idUtente}
        </p>

        <p>
            <br>ROVELLI ANDREA
            <br>@GET
            <br>http://localhost:8080/spesa/lista?rifRichiesta={id}
            <br>@POST 
            <br>http://localhost:8080/spesa/lista
            <br>@PUT 
            <br>http://localhost:8080/spesa/updLista
        </p>
    </center>
</body>
</html>
