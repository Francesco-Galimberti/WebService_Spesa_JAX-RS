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

        <h1>http://localhost:8080/WS_SPESA/spesa</h1>


        <h1>TOSETTI LUCA</h1>
        <h4>@GET</h4>
        <label>
            /risposte
            <br>/prodotto?genere={genere}&nome={nome}...
        </label>
        <h4>@POST </h4>
        <label>/prodotto</label>
        <h4>@PUT </h4>
        <label>/prodotto/{idProdotto}</label>
        <h4>@DELETE </h4>
        <label>/prodotto/{idProdotto}</label>


        <h1>SPANGARO FRANCESCO</h1>
        <h4>@GET</h4>
        <label>
            /richiestaXML/{id}
            <br>/richiestaJSON/{id}
        </label>
        <h4>@POST</h4> 
        <label>/utenteXML
            <br>/utenteJSON
            <br>/richiestaXML
            <br>/richiestaJSON
        </label>
        <h4>@DELETE </h4>
        <label>/lista?id={rifRichiesta}</label>


        <h1>GALIMBERTI FRANCESCO</h1>
        <h4>@GET</h4>
        <label>
            /utenti/utente?id={idUtente}<br>
            /utenti/utente?username={username}<br>
            /utenti<br>
            /utenti?nome={nome}&regione={regione}...
        </label>
        <h4>@POST </h4>
        <label>/risposta</label>
        <h4>@PUT</h4>
        <label>/utenti/{idUtente}</label>
        <h4>@DELETE </h4>
        <label>/richieste/{idRichiesta}/{idUtente}</label>



        <h1>ROVELLI ANDREA</h1>
        <h4>@GET</h4>
        <label>/lista?rifRichiesta={id}</label>
        <h4>@POST </h4>
        <label>/lista</label>
        <h4>@PUT </h4>
        <label>/updLista</label>

    </center>
</body>
</html>
