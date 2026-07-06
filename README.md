# Secured Stateless Example

Exemple d'application **Spring Boot** illustrant une sécurité **stateless** basée sur des
**JWT** (JSON Web Tokens) avec **Spring Security**. Le projet montre l'authentification,
l'enregistrement d'utilisateurs, la gestion des rôles et un mécanisme de « magic link ».

> ℹ️ Ce projet utilise **Maven** (et non Gradle). Le *wrapper* Maven (`./mvnw`) est fourni :
> aucune installation de Maven n'est nécessaire.

---

## 🧰 Stack technique

| Élément            | Version / Détail                                  |
|--------------------|---------------------------------------------------|
| Java               | **21** (le code cible le langage niveau 17)       |
| Spring Boot        | 3.2.0                                             |
| Build              | Maven (via `./mvnw`, Maven Wrapper 3.9.9)         |
| Base de données    | MySQL (prod/dev) **ou** H2 en mémoire (démo)      |
| Migrations         | Flyway                                            |
| Sécurité           | Spring Security + JWT (JJWT 0.11.2, HS512)         |

### Prérequis

- Un **JDK 21** (Temurin/OpenJDK). Sur cette machine il est installé dans
  `~/.jdks/jdk-21.0.11+10`. Pensez à exporter `JAVA_HOME` avant de lancer Maven :

  ```bash
  export JAVA_HOME=~/.jdks/jdk-21.0.11+10
  ```

  > Spring Boot 3.2 est prévu pour Java 17–21. N'utilisez pas Java 25 pour l'exécution.

---

## 🗄️ Choisir sa base de données (3 chemins au choix)

La base de données est sélectionnée via un **profil Spring**. Chaque profil est **autonome
et versionné** (datasource + JWT + CORS), donc tout fonctionne directement après un
`git clone`, sans créer le moindre fichier.

| Profil     | Base de données      | Externe requis ?          | Fichier de config                      |
|------------|----------------------|---------------------------|----------------------------------------|
| `h2`       | H2 (en mémoire)      | ❌ Rien                    | `application-h2.yml`                    |
| `mysql`    | MySQL                | ✅ `compose.mysql.yaml`    | `application-mysql.yml`                 |
| `postgres` | PostgreSQL           | ✅ `compose.postgres.yaml` | `application-postgres.yml`              |

> Chaque base a ses propres migrations Flyway (`db/migration/` pour MySQL,
> `db/h2/`, `db/postgres/`) car la syntaxe SQL diffère.
> Les trois créent le même schéma et les mêmes utilisateurs de test.

> ⚠️ Sélectionnez **toujours** un profil. Sans profil, l'application cherche un
> `application.properties` (ignoré par Git, absent après un clone).

### Option A — H2 (recommandé, aucun setup) 🚀

```bash
export JAVA_HOME=~/.jdks/jdk-21.0.11+10
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Base en mémoire : aucune installation de MySQL/PostgreSQL ni de Docker.

### Option B — MySQL 🐬

```bash
# 1. Démarrer la base (docker ou podman)
docker compose -f compose.mysql.yaml up -d

# 2. Lancer l'application
export JAVA_HOME=~/.jdks/jdk-21.0.11+10
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Option C — PostgreSQL 🐘

```bash
# 1. Démarrer la base (docker ou podman)
docker compose -f compose.postgres.yaml up -d

# 2. Lancer l'application
export JAVA_HOME=~/.jdks/jdk-21.0.11+10
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Dans les trois cas, l'application démarre sur **http://localhost:8080**.

> 💡 Avec **Podman** au lieu de Docker : `podman compose -f compose.mysql.yaml up -d`
> (ou lancez un conteneur directement, voir les commentaires en tête des fichiers `compose.*.yaml`).

---

## 🖥️ Lancer depuis IntelliJ IDEA

1. Ouvrez le dossier du projet : IntelliJ détecte `pom.xml` et l'importe comme projet **Maven**
   (acceptez le chargement du build Maven / « Trust project »).
2. Le SDK du projet est réglé sur **`temurin-21`** (Java 21), niveau de langage 17.
3. Une configuration de lancement **« WebsiteApplication »** (Spring Boot) est déjà fournie :
   cliquez simplement sur ▶.
   - Renseignez le profil voulu dans le champ *Active profiles* de la configuration :
     `h2`, `mysql` ou `postgres`.
   - Pour `mysql` / `postgres`, démarrez d'abord le conteneur correspondant
     (`docker compose -f compose.mysql.yaml up -d`, etc.).
   - `h2` ne nécessite aucun conteneur.

---

## 🔨 Compiler et tester

```bash
export JAVA_HOME=~/.jdks/jdk-21.0.11+10

./mvnw clean compile      # compilation
./mvnw clean package      # construit le JAR exécutable dans target/
./mvnw test               # tests (nécessite une base : profil h2 conseillé)
```

Exécuter le JAR packagé :

```bash
java -jar target/secured-stateless-example-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2
```

---

## 👤 Utilisateurs de test

Insérés automatiquement par Flyway, quel que soit le profil (`h2`, `mysql`, `postgres`) :

| Utilisateur | Rôle          |
|-------------|---------------|
| `admin1`    | `ROLE_ADMIN`  |
| `student1`  | `ROLE_USER`   |
| `teacher1`  | `ROLE_TESTER` |

(Les trois partagent le même mot de passe BCrypt de démonstration.)

---

## 🔌 Principaux endpoints

| Méthode | Chemin                 | Description                                   |
|---------|------------------------|-----------------------------------------------|
| POST    | `/api/login`           | Authentification, renvoie un JWT              |
| POST    | `/api/users/register`  | Enregistrement d'un nouvel utilisateur        |
| GET     | `/test`                | Endpoint de test                              |
| POST    | `/test`                | Endpoint de test                              |
| GET     | `/test/security`       | Endpoint protégé (nécessite un JWT valide)    |
| GET     | `/admin/users/test`    | Réservé aux administrateurs                   |
| GET     | `/actuator/health`     | Santé de l'application (protégé)              |

> Une **collection Bruno** est fournie dans le dossier `bruno/`.
> Ouvrez-la dans [Bruno](https://www.usebruno.com/), sélectionnez l'environnement
> **Local** (`connectionPath = http://localhost:8080`), puis lancez les requêtes.
> En ligne de commande : `cd bruno && bru run . --env Local -r`.
> La requête *Connection as Admin* capture le JWT dans la variable `userToken`,
> réutilisée automatiquement par les requêtes protégées.

Sans jeton valide, les endpoints protégés renvoient **HTTP 403** : c'est le comportement
attendu, cela prouve que Spring Security est actif.

---

## 📁 Structure du projet

```
src/main/java/com/security/
├── WebsiteApplication.java        # point d'entrée
├── config/                        # Spring Security + filtres/fournisseur JWT
├── controller/                    # endpoints REST
├── dto/                           # objets de transfert (login, réponse auth)
├── entity/                        # entités JPA (User, Role)
├── repository/                    # repositories Spring Data
└── service/                       # logique métier + UserDetailsService

src/main/resources/
├── application-h2.yml             # profil h2  (base en mémoire)
├── application-mysql.yml          # profil mysql
├── application-postgres.yml       # profil postgres
└── db/
    ├── migration/                 # profil mysql
    │   ├── V1__Initial_schema.sql
    │   ├── V2__Magic_Link_Installation.sql
    │   └── testdata/V1001__...    # données de test
    ├── h2/                        # profil h2  (équivalents compatibles H2)
    └── postgres/                  # profil postgres (équivalents compatibles PostgreSQL)

compose.mysql.yaml                 # conteneur MySQL
compose.postgres.yaml              # conteneur PostgreSQL
```

---

## 🔒 Note de sécurité

Le secret JWT et les identifiants présents dans ce dépôt sont **uniquement destinés au
développement local**. Ne les réutilisez jamais en production : définissez-les via des
variables d'environnement ou un gestionnaire de secrets.
