FROM openjdk:17-slim

# Installer Xvfb et les dépendances nécessaires
RUN apt-get update && apt-get install -y xvfb && rm -rf /var/lib/apt/lists/*

# Configurer Xvfb
ENV DISPLAY=:99
CMD Xvfb :99 -screen 0 1024x768x16 & java -cp .:MaVille.jar ui.MainMenu