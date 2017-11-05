# BBC Url Tester


### Prerequisites 
##### To Run
- Java 8 or greater
- JDK 1.8.0_131 or greater

### About

This is a java application to test a provided list of line end separated urls and to collect the Content Length, Date and status headers. This information is printed to standard output in a json format as follows:

```$json
{
  "Url": "https://google.com",
  "Status_code": 302,
  "Content_length": 262,
  "Date": "Tue, 25 Jul 2017 17:00:55 GMT"
 }
 ```
 
 Errors are reported for a url as follows: 
 ```$json
 {
  "Url": "bad://address",
  "Error": "invalid url"
 }
 ```

The Content length or Date field can have `null` as the value as they may not be returned by an endpoint.

Status code report has the following structure:
```$json
[
  {
    "Status_code": -1,
    "Number_of_responses": 1
  },
  {
    "Status_code": 200,
    "Number_of_responses": 2
  },
  {
    "Status_code": 301,
    "Number_of_responses": 3
  }
]
```

Errors are reported with a status code of -1.

##### For development
- An internet connection to download dependencies through gradle

## Quick-start

At the root of this project is the bbc-url-tester.jar file. This is a prepackaged executable of the code. This can be executed using the following command.

`java -jar build/libs/bbc-url-tester-1.0-SNAPSHOT.jar <insert space sperated args here>`

`java -jar build/libs/bbc-url-tester-1.0-SNAPSHOT.jar "https://www.bbc.co.uk\nhttps://www.google.co.uk"`

Quick description of all arguments and flags: 

```
-h | --help   prints help message
-t | --timeout   Set a timeout in milliseconds for connecting and reading urls provided
urls   A new line separated list of urls to test
```

## Set-up

**All of these steps assume you are at the root directory for this project**

`./gradlew`

Run the gradlew script file in the root of the project for your OS to download a project copy of gradle. This can then be used to build and run this project.


### Dev Run

`./gradlew run -PappArgs="[<insert comma seperated, quoted args here>]"`

Example: `./gradlew run -PappArgs="['--timeout', '5000', 'https://www.bbc.co.uk/nhttps://www.google.co.uk']"`

This application is built using Gradle. Gradle manages the dependices of the project and downloads them from a publicly accessible repository.
To build 

### Test

`./gradlew test`

Will run all of the unit tests in this project.


#### Useful Links

https://www.iana.org/assignments/message-headers/message-headers.xml#perm-headers

List of all response headers that are officially recognised. The names defined here are what are used to try and extract the necessary information from responses from provided URLs.

Git repository link:
https://github.com/curlyObject/bbc-url-test


