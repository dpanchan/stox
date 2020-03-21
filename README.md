## Stock Trading Application (Stox!)

* This application lets a user interact with the stock trading system. 
* It allows user to sell/buy stocks, subscribe/unsubscribe stocks, check balance/ add deposit.
* When user subscribes the stock, application sends notifications to user when it reaches the threshold limit.

### Requirements
1. Java 1.8
2. Maven
3. STS or any Java IDE (to browse code)
4. Terminal or Command prompt
5. Uses in-memory H2 database, no other DB required.

### Directory structure:
1. Eureka - Eureka service registry server
2. Stock - Microservice which has stock information, processes buy/sell orders. It simulates stock prices every 10 seconds.
3. User - Microservice which helps to register / login users, lets you subscribe to stocks and maintains your cash.
4. Client - A CLI Spring boot app that provides API to use stock and user applications

### Steps to run:

Command to start every microservice/app : `mvn clean spring-boot:run`

1. Start Eureka server which is configured with default port 8761 with above command.
2. Start Stock service with above command, let it register with Eureka server and starts simulating stocks.
3. Start User service with above command and let it register with Eureka server, it registers a test user.
4. If both stock and user applications are running, then only start client application to interact with Stox.

After you start Client, perform below steps.

1. User can login or register (if new user) to the application
2. Once authorized, user is given options to select to perform CRUD operations on stocks usage.

**Note**: Dummy user details to login:
```
Username: test
Password : 123
```

Or you can register a new user.

Please test this app and let me know any feedback. Criticism is welcome.






