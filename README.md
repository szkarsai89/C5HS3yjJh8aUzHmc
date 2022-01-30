# Project Title

Client Enrollment Validator

## Description

This repository is contains a multi-module maven application 'client-enroll'. 
This application is separated to three main part:
* API
* Service
* APP

Where the API contains the Rest interface definitions in OpenAPI compatible format, the required model definitions also in YAML format.
The Rest module purpose is to generate the JAVA classes from the previous two.
The Rest-Client module is used to generate the code also based on previous two but for used by clients (For example: Angular)

The Service module is contains the service layer required definitions and implementations.

The App is the web layer where the Rest interface(s) are implemented.

## Getting Started

### Dependencies

* Maven 3.6.3
* Java 11
* Docker (optional)

### Installing

* Checkout from git: https://github.com/szkarsai89/C5HS3yjJh8aUzHmc.git
* Check and modify the 'application.yaml' as you want the server ports in three application main resources folder
* Execute 'maven clean install' in three directory 

### Executing program

* In case of local execution with pure Java use the next steps
```
java -jar ./risk-emu/target/client-enroll-riskcalculator-emulator-0.0.1-SNAPSHOT.jar 
```

```
java -jar ./existence-emu/target/client-enroll-existence-emulator-0.0.1-SNAPSHOT.jar 
```

```
java -jar ./client-enroll/app/target/client-enroll-app-0.0.1-SNAPSHOT.jar 
```

* In case of want to execute in Docker from local build use the next step
```
docker-compose -f .\docker-compose-local.yaml up
```

* In case of want to execute in Docker but from DockerHub images without local build use the next step
```
docker-compose -f .\docker-compose.yaml up
```
## Help

To be able to try it out open in browser the
```
http://localhost:8090/swagger-ui/index.html
```

The Risk Emulator is use the last two digit of IdCard number to give it back as Risk level.
For example if the card number is "ASDFGH12" the risk level will be 12.

The Customer Exist Emulator is use the first letter of IdCard number to give it bask as Boolean for query existence.
The exact rule is if the card number is start with 'E' then the result will be 'True' otherwise 'False'

The currently configured IdCard Number pattern is 'A-Z' in 6 length and '0-9' in 2 length.

## Possible Improvements
* Introduce Spring Security to secure the service
* Use Session or Database to store the validation result
* The Mid and High Risk Level change from hard coding to configurable values
* Create a separeted configuration file for IdCard pattern, RiskLevel and for any further configuration
* Add configurable loggings

## Authors

Szabolcs Karsai  
szabolcs.karsai@wup.hu

## Version History

* 0.0.1
    * Initial Release