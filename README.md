# Pokemon Service
Fullstack Spring webapp about pokemons where you can find pokemons, see their stats and add 1 pokemon to your favourites if you're login.

### Section links

|[General](#general)|[Usage guide](#usage-guide)|[Used technologies](#used-technologies)|[Demo](#demo)|[Version history](#version-history)|[Feedback](#feedback)|
|:-----------------:|:-------------------------:|:-------------------------------------:|:-----------:|:---------------------------------:|:-------------------:|

### General
Application provides 3 major functionalities:
1. Pokemon database where anyone can search for pokemons and their stats
2. Adding pokemon as logged user and the ability to see which pokemons are the most liked among all users
3. Secured registration and logging to the website

Pokemon database has 905 pokemons up to generation 8. In the database user can view pokemon Pokedex id, name, type/types, evolution (all possibilities), likes (how many users added pokemon to favourites) with the ability to sort returned values and search by pokemon name. When clicked on a pokemon, all information about it is displayed along with a photo. From the level of a single pokemon, user can go to its evolution (if there is any) or add it to favorites.

As mentioned user can see the ranking of the most liked pokemons (20). Additionally user can check its favourite pokemon in account menu and can change it to another Pokemon at will.

Register and Login operate on the basis of database authentication. User's registration and login credentials are validated and if they fail, an appropriate error is displayed. Login also has remember-me feature. Every user has the ability to change its password. There is currently only one role **USER** with permission to like pokemons.

The application consists of many html pages, connected with the thymeleaf engine. In the case of style, Bootstrap was used in conjunction with a custom stylesheet. Despite using Bootstrap, application doesn't support proper responsiveness at the moment and works best on desktop screen. Some of the pages also work with small JavaScript file, that has couple supportive functions.

The exact operation of the entire application is presented in the [Demo](#demo) tab in the form of a video

### Usage guide

- **Running app with Docker:**

Open any bash in project root and use this set of commands:

```./mvnw clean package -DskipTests```

```docker build -f Dockerfile -t pokemon-service-fullstack-docker:v1 .```

```docker-compose up -d```

This will start the application with initial data (905 pokemons and 1 superuser).

- **Running app without Docker:**

Application works with PostgresSQL database. In order to run this app you need have Postgres client installed on your machine and provide some empty database in ***/src/main/resources/application.properties*** file. After that you can start application manually. Initial data will be entered into database. The application can later work in update mode by changing property `spring.jpa.hibernate.ddl-auto=create-drop` to `spring.jpa.hibernate.ddl-auto=update` in ***/src/main/resources/application.properties*** file.

- **Running tests:**

In order to run tests in ***/test*** directory, you have to provide some empty database in ***/test/resources/application.properties*** file. The database **must be** different from the one used in running application without docker.

### Used technologies
The following technologies were used in the production of the application:
- Spring security
- Spring Data JPA
- Spring validation
- PostgreSQL
- REST API
- Thymeleaf
- Bootstrap

In case of written tests for the designed application, the following technologies were used:
- JUnit
- AssertJ
- Mockito
- Hamcrest

### Demo
Below is a video showing an example of using all of the functionalities offered by the application.

https://user-images.githubusercontent.com/95027426/211678387-e3abbc21-938d-471e-b396-e8e2a3681fe3.mp4

### Version history
**1.0.1**

- docker cooperation

**1.0.0 - Initial stable version**

- pokemons with photo, id, name, type/types, evolution and likes
- register/login by username and password
- no responsiveness

### Feedback
If you have any questions or comments regarding the app, please place them in Discussion panel or contact me directly.
