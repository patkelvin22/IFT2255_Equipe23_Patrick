require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mysql = require('mysql2');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const db = mysql.createConnection({
    host: process.env.MYSQL_HOST,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_PORT,
    connectTimeout: 10000, // Timeout de connexion pour éviter les erreurs
});

db.connect((err) => {
    if (err) {
        console.error('Erreur de connexion à la base de données :', err);
        process.exit(1); 
    }
    console.log('Connecté à la base de données MySQL');
});

app.get('/', (req, res) => {
    res.send('Bienvenue dans l\'API MaVille');
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Serveur API démarré sur http://localhost:${PORT}`);
});