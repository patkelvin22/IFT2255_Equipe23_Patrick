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

// Connecter à la base de données
db.connect((err) => {
    if (err) {
        console.error('Erreur de connexion à la base de données :', err);
        process.exit(1);
    }
    console.log('Connecté à la base de données MySQL');
});

// Endpoint racine
app.get('/', (req, res) => {
    res.send('Bienvenue dans l\'API MaVille. Utilisez les endpoints /residents et /intervenants pour voir les données.');
});

// Endpoint pour récupérer tous les résidents
app.get('/residents', (req, res) => {
    const query = 'SELECT * FROM Residents';
    db.query(query, (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération des résidents :', err);
            res.status(500).send('Erreur interne du serveur');
        } else {
            res.json(results);
        }
    });
});

// Endpoint pour récupérer tous les intervenants
app.get('/intervenants', (req, res) => {
    const query = 'SELECT * FROM Intervenants';
    db.query(query, (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération des intervenants :', err);
            res.status(500).send('Erreur interne du serveur');
        } else {
            res.json(results);
        }
    });
});

// Endpoint pour récupérer un résident spécifique par son ID
app.get('/residents/:id', (req, res) => {
    const query = 'SELECT * FROM Residents WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération du résident :', err);
            res.status(500).send('Erreur interne du serveur');
        } else if (results.length === 0) {
            res.status(404).send('Résident non trouvé');
        } else {
            res.json(results[0]);
        }
    });
});

// Endpoint pour récupérer un intervenant spécifique par son ID
app.get('/intervenants/:id', (req, res) => {
    const query = 'SELECT * FROM Intervenants WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération de l\'intervenant :', err);
            res.status(500).send('Erreur interne du serveur');
        } else if (results.length === 0) {
            res.status(404).send('Intervenant non trouvé');
        } else {
            res.json(results[0]);
        }
    });
});

// Lancer le serveur
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Serveur API démarré sur http://localhost:${PORT}`);
});