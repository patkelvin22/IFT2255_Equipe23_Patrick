# IFT2255_Equipe23
MaVille est une application innovante dédiée à la gestion des travaux publics et privés dans la ville de Montréal. Elle vise à renforcer la communication entre les résidents et les différents acteurs impliqués, qu'ils soient des entreprises privées, des organismes publics ou des particuliers. Son objectif principal est d'optimiser la coordination des travaux afin de réduire les perturbations dans la vie quotidienne des résidents.

Le projet offre plusieurs fonctionnalités clés, notamment :

- Suivi en temps réel des travaux : Les résidents peuvent consulter les informations actualisées sur les travaux en cours et à venir dans leur quartier.
- Gestion des utilisateurs : Les utilisateurs peuvent s'inscrire et se connecter en tant que résidents ou intervenants, chaque rôle offrant des fonctionnalités adaptées.
- Planification collaborative : Les résidents et les intervenants peuvent planifier et coordonner ensemble les projets de travaux, favorisant une meilleure prise en compte des besoins locaux.
- Accès transparent aux informations de travaux : Les résidents ont un accès direct aux détails des travaux réalisés dans la ville, améliorant ainsi la transparence et la sensibilisation.
- Soumission de requêtes : Les résidents peuvent proposer des travaux ou signaler des problèmes nécessitant des interventions, permettant ainsi un dialogue direct avec les responsables.

# Prototype 
Ce protoype est conçu afin de permettre aux utilisateurs de s'inscrire et de se connecter 
en tant que résident ou intervenant. Les utilisateurs peuvent soit consulter et modifier leur profil, 
participer à une planification plus inclusive et collaborer entre eux afin de mieux gérer les travaux urbains.

Structure des fichiers :
- Boundary : Ce dossier contient les classes relatives à l’interface utilisateur. Elles définissent la structure des menus utilisés par les différents types d’utilisateurs.  

    - AbstractMenu.java : Classe abstraite fournissant les fonctionnalités communes aux menus des résidents et intervenants(affichage etnavigation).
    - IntervenantMenu.java : Menu spécifique aux intervenants, contenant les options comme soumettre un projet de travaux ou consulter les travaux existants.
    - MainMenu.java : Menu principal où l’utilisateur peut choisir de s’inscrire ou se connecter.
    - ResidentMenu.java : Menu spécifique aux résidents, permettant des actions comme soumettre une requête ou consulter des travaux.
- controllers : Ce dossier regroupe les classes qui contrôlent la logique de l’application. Elles gèrent les interactions entre l’interface utilisateur, les entités et la persistance des données.

    - ProjectsServices.java : Classe responsable des services liés aux travaux, comme la soumission, la mise à jour et la recherche de projets de travaux.
    - UserServices.java : Classe contenant la logique relative aux utilisateurs, comme l’inscription, la connexion et la gestion des profils.
    - XMLDatabase.java : Gère la persistance des données dans des fichiers XML, comme les utilisateurs, les travaux et les requêtes de travaux.

- Entity : Ce dossier contient les classes représentant les entités principales de l’application. Elles sont utilisées pour modéliser les données.

    - ManageUsers.java : Classe centrale pour la gestion des utilisateurs, permettant de charger, sauvegarder, et mettre à jour les données des résidents et intervenants.
    - User.java : Classe abstraite commune à toutes les entités utilisateur, comme les résidents et les intervenants.
    - Category.java : Classe énumérative définissant les différentes catégories de travaux ou d’intervenants.
    - Intervenant.java : Classe représentant un intervenant avec des attributs comme le nom, le type d’entreprise, et les projets qu’il gère.
    - Resident.java : Classe représentant un résident avec des attributs comme le nom, l’adresse et les requêtes de travaux soumises.
    - WorkProject.java : Classe représentant un projet de travaux routiers, avec des attributs comme le titre, la catégorie, et le statut.
    - WorkRequest.java :  Classe représentant une requête de travaux soumise par un résident.
    - Obstacles.java : Classe représentant les entraves associées à un projet de travaux.

- Resources : Ce dossier contient les fichiers nécessaires au fonctionnement de l’application, comme les fichiers XML pour la persistance des données et les fichiers texte pour l’affichage.
    
    - intervenantHomePage.txt : Contient probablement les informations ou instructions affichées sur la page d’accueil des intervenants.
    - notifications.txt : Fichier texte utilisé pour gérer ou afficher les notifications.
    - residentHomePage.txt : Contient les informations ou instructions affichées sur la page d’accueil des résidents.
    - users.xml : Fichier XML contenant les données persistées des utilisateurs (résidents et intervenants).
        
Pour lancer l'application :
- Assurez vous que java est bien installé sur votre machine ( java -version)
- Exécutez la commande suivante dans le dossier 'prototype' :
java -jar MaVille.jar


Pour naviguer dans les différent menus, entrez un des chiffres affichés 
dans la console.

3 Résidents et intervenants déjà créés. Chaque résident a une requête de travail ajouté à son profil et chaque intervenant a un projet de travail ajouté à son profil: 

- Pour se connecter en tant que résident:
    - email : "johnDoe@gmail.com"
    - Mot de passe : 123456

    - email : "aliceWonder@gmail.com"
    - Mot de passe : password123

    - email : "bobBuilder@yahoo.com"
    - Mot de passe : builder456

- Pour se connecter en tant qu'intervenant:
    - email : "charlieBrown@gmail.com"
    - Mot de passe : charlie789

    - email : "dt@protonmail.com"
    - Mot de passe : david1010

    - email : "janeSmith@outlook.net"
    - Mot de passe : 123456
      
Pour effectuer les tests :
- Exécutez la commande suivante dans le dossier IFT2255_Equipe23:
      mvn test
