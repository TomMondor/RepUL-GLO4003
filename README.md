# Telephony WS

## Qu'est-ce que c'est

Un exemple de projet pour créer un API Web Rest en utilisant Java, Jetty et Jersey seulement.

## Contexte

Voici une structure de projet qui n'utilise pas de plate-forme d'injection de dépendance et qui explicite
la création de l'application et de ses composantes. C'est une application petite, mais complète, qui sera une base de
projet pour un travail à l'Université. La couverture des tests est minimale pour encourager les étudiants à explorer.
L'exercice est de comprendre comment fonctionne le tout pour mieux bâtir une application en utilisant
de l'injection de dépendance.

## Comment l'utiliser
* Vous pouvez installer manuellement Java 17 et mettre à jour la variable JAVA_HOME. Cependant, nous vous recommandons d'utiliser
  [SDKMAN](https://sdkman.io/) pour gérer Java et plusieurs outils et frameworks reliés à la JVM. Cet outil va vous faciliter énormément
  la gestion des versions de Java et outils annexes surtout si vous en voulez plusieurs sur votre machine.
* Avec Java 17 et Maven d'installé et de configuré;
  * Pour lancer l'aplication:
    * Dans un terminal, exécutez start.sh si vous êtes sur Linux / OSX
    * Dans un terminal, exécutez start.bat si vous êtes sur Windows
    * Dans un IDE, exécutez la classe `TelephonyWsMain` en tant que "Java Application"
  * Une fois démarré, vous trouverez les données aux URLs suivantes:
    * http://localhost:8080/api/telephony/contacts
    * http://localhost:8080/api/telephony/calllogs
  * Afin de valider votre projet, vous pouvez:
    * Exécuter les testes unitaires avec `mvn test`
    * Exécuter tous les tests (unitaires et d'intégrations) avec `mvn integration-test`
    * Exécuter toutes les vérifications (test, dependency-check, etc...) et produire un artifact pour votre application (se trouvant sous
      `target/application.jar`) avec `mvn verify` que vous pouvez juste invoquer directement avec un `java -jar target/application.jar`
