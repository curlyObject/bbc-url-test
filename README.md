# BBC Url Tester


### Prerequisites 
##### To Run
- Java 8 or greater
- JDK 1.8.0_131 or greater

##### For development
- An internet connection to dowload dependencies through gradle

## Quick-start

At the root of this project is the bbc-url-tester.jar file. This is a prepackaged executable of the code. This can be executed using the following command.

`java -jar build/libs/bbc-url-tester-1.0-SNAPSHOT.jar <insert space sperated args here>`

## Set-up

**All of these steps assume you are at the root directory for this project**

`./gradlew`

Run the gradlew script file in the root of the project for your OS to download a project copy of gradle. This can then be used to build and run this project.


### Dev Run

`./gradlew run -PappArgs="[<insert comma seperated, quoted args here>]"`

Example: `./gradlew run -PappArgs="['foo', 'bar']"`

This application is built using Gradle. Gradle manages the dependices of the project and downloads them from a publicly accessible repository.
To build 


### Test

`./gradlew test`

Will run all of the unit tests in this project.



