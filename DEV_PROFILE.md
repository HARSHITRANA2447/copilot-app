Development profile and H2 console

This project includes a `dev` Spring profile that configures an in-memory H2 database and enables the H2 web console for development.

How to run with the dev profile

Windows PowerShell (temporary for current session):

```powershell
# set JAVA_HOME for the current session (replace path with your JDK installation path)
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:PATH = $env:JAVA_HOME + '\\bin;' + $env:PATH

# build
.\mvnw package

# run with the dev profile
.\mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# OR if running the jar
java -jar .\target\copilot-app-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

Accessing the application and H2 console

- Application: http://localhost:9090/
- H2 console: http://localhost:9090/h2-console
  - JDBC URL: jdbc:h2:mem:testdb
  - User: sa
  - Password: (leave blank)

Notes

- The H2 console is enabled only for the `dev` profile. Do not enable it in production.
- If you see a JAVA_HOME error when running `./mvnw`, make sure you have a JDK installed and set the `JAVA_HOME` environment variable.
