require('dotenv').config();
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const mysql = require('mysql2');
const morgan = require('morgan'); // Pour journaliser les requêtes HTTP

const app = express();

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(morgan('dev')); // Journalisation des requêtes

// Vérification des variables d'environnement
const requiredEnv = ['MYSQL_HOST', 'MYSQL_USER', 'MYSQL_PASSWORD', 'MYSQL_DATABASE', 'MYSQL_PORT'];
requiredEnv.forEach((envVar) => {
    if (!process.env[envVar]) {
        console.error(`Erreur : La variable d'environnement ${envVar} n'est pas définie.`);
        process.exit(1);
    }
});

// Configuration de la base de données
const db = mysql.createConnection({
    host: process.env.MYSQL_HOST,
    user: process.env.MYSQL_USER,
    password: process.env.MYSQL_PASSWORD,
    database: process.env.MYSQL_DATABASE,
    port: process.env.MYSQL_PORT,
    connectTimeout: 10000, // Timeout de connexion pour éviter les erreurs
});

// Connexion à la base de données
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
            return res.status(500).json({ error: 'Erreur interne du serveur' });
        }
        res.json(results);
    });
});

// Endpoint pour récupérer tous les intervenants
app.get('/intervenants', (req, res) => {
    const query = 'SELECT * FROM Intervenants';
    db.query(query, (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération des intervenants :', err);
            return res.status(500).json({ error: 'Erreur interne du serveur' });
        }
        res.json(results);
    });
});

// Endpoint pour récupérer un résident spécifique par son ID
app.get('/residents/:id', (req, res) => {
    const query = 'SELECT * FROM Residents WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération du résident :', err);
            return res.status(500).json({ error: 'Erreur interne du serveur' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Résident non trouvé' });
        }
        res.json(results[0]);
    });
});

// Endpoint pour récupérer un intervenant spécifique par son ID
app.get('/intervenants/:id', (req, res) => {
    const query = 'SELECT * FROM Intervenants WHERE id = ?';
    db.query(query, [req.params.id], (err, results) => {
        if (err) {
            console.error('Erreur lors de la récupération de l\'intervenant :', err);
            return res.status(500).json({ error: 'Erreur interne du serveur' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Intervenant non trouvé' });
        }
        res.json(results[0]);
    });
});

// Middleware pour gérer les routes non trouvées
app.use((req, res) => {
    res.status(404).json({ error: 'Route non trouvée' });
});

// Middleware pour gérer les erreurs internes
app.use((err, req, res, next) => {
    console.error('Erreur non gérée :', err);
    res.status(500).json({ error: 'Erreur interne du serveur' });
});

// Lancer le serveur
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`Serveur API démarré sur http://localhost:${PORT}`);
});