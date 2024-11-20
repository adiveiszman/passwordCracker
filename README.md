# Password Cracker

A password-cracking service that receives a file containing hashes and outputs the corresponding cracked passwords. This project demonstrates distributed password cracking by dividing workload between a master server and several minion servers.

## Getting Started

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Installation

1. **Clone the Repository**:

   ```sh
   git clone https://github.com/adiveiszman/passwordCracker.git
   cd passwordCracker
   ```

2. **Install Dependencies**:

   Run the following command to clean, compile, and package the project:

   ```sh
   mvn clean install
   ```

3. **Configure Application Properties**:

   Update `application.properties` for the master server:

   - Navigate to `master-service/src/main/resources/application.properties`.
   - Set `minion.endpoints` to include the URLs of your minion servers.
   - Configure any other necessary properties for your specific environment.

### Running the Services

#### Running the Master Server

To start the master server, navigate to the `master-service` directory and run:

```sh
cd master-service
mvn spring-boot:run
```

Alternatively, run the packaged JAR file:

```sh
java -jar target/master-service-<version>.jar
```

#### Running the Minion Servers

To run a minion server, navigate to the `minion-service` directory and execute:

```sh
cd minion-service
mvn spring-boot:run
```

If you want to run multiple instances of the minion server, you can do so by copying the configuration in your IDE (e.g., IntelliJ) and adding a JVM option to specify a different port (e.g., `-Dserver.port=8082`).

### Using the REST API

#### Master Service API

To start the password-cracking process, use a REST client like Postman to send a POST request to the master server:

- **Endpoint**: POST [http://localhost:8080/master/task/initiate](http://localhost:8080/master/task/initiate)
- **Body**: Attach your hash file as the request body (the file should be a simple text file where each line contains one hash).

#### Minion Service API

To initiate the password-cracking process on a minion server, use the following endpoint (this should be used only for directly contacting a minion server, not for managing the entire cracking process):

- **Endpoint**: POST [http://localhost:8081/minion/crack](http://localhost:8081/minions/crack)
- **Body**: Provide the hash and the range of potential passwords to be checked.

  Example input:
  ```json
  {
      "hash": "be3ddca3d0b06596aea3ebd4f5e6ffc2",
      "startRange": 10000000,
      "endRange": 26880728
  }
  ```
  
  This input will check the phone numbers range from `051-0000000` to `052-6880728`.

The master server will distribute the workload to the minion servers to crack the passwords concurrently.

## Additional Information

- The service cracks MD5 hashes of phone numbers in the format `05X-XXXXXXX`.
- You can run multiple minion instances to improve performance and handle larger workloads.

### Troubleshooting

- Ensure all minion endpoints are correctly set in the master configuration.
- Make sure the minion servers are running and reachable from the master server.
