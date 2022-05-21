# P8-TOURGUIDE

P8 - OpenClassroom - Améliorez votre application avec des systèmes distribués - MURER ERIK

## Résumé

TourGuide permet aux utilisateurs de voir quelles sont les
attractions touristiques à proximité et d’obtenir des réductions sur les séjours d’hôtel
ainsi que sur les billets de différents spectacles.

## Technologies

- Java 1.8 JDK
- Gradle 7.4.2
- Spring 2.6.6
- Docker

## Specifications techniques

TourGuide se décompose en 4 microservice :
1. TourGuide - https://github.com/ErikM06/OC_P8_TourGuide.git
2. GpsUtil - https://github.com/ErikM06/OC-P8-TourGuide-Gps-Service.git
3. RewardsCentral - https://github.com/ErikM06/OC-P8-TourGuide-Reward-Central.git
4. TripPricer - https://github.com/ErikM06/OC-P8-TourGuide-Trip-Pricer.git

## Lancer l'application

### Gradle :
1. Builder l'application

`$ gradle build̀`

2. Run l'application
`$ gradle bootRun`

### Docker :
1. Builder l'application avec gradle (n° 1 vu ci-dessus).

2. Créer une image docker pour chaque micro-service dans leur dossier racine, a l'aide de la commande suivante :
`$ docker build -t NAME_OF_YOUR_IMAGE:TAGVERSION .` 

3. Dans le dossier racine de TourGuide, utiliser Docker-Compose pour assembler les images et les lancer dans le même conteneur. Modifier le nom de chaque image dans le docker-compose.yaml afin de les faires correspondre a vos nom d'image et version.
`$ docker-compose up`

## Endpoints 

    GET - Index http://localhost:8080/

    GET - retourne la localisation de l'user 
    http://localhost:8080/getLocation
    Param : userName

    GET - retourne les 5 attractions les plus proches
    http://localhost:8080/getNearbyAttractions
    Param: userName

    GET - retourne la position actuel de tous les users 
    http://localhost:8080/getAllCurrentLocations

    GET - retourne les rewards points de l'user
    http://localhost:8080/getRewards
    Param: userName

    GET - retourne les trip deals pour un user
    http://localhost:8080/getTripDeals
    Param: userName

## Tester 

- Pour tester l'application

`$ gradle test`

- Pour générer un rapport Jacoco

`$ gradle jacocoTestReport`

- Pour générer un rapport Jacoco de Couverture

`$ gradle jacocoTestCoverageVerification`
