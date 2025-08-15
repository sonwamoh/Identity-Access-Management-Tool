<img width="1085" height="803" alt="Screenshot 2025-08-14 215207" src="https://github.com/user-attachments/assets/3fe8959b-050b-40e5-b4d7-68a6108f2bbb" />


# Features

Submit access requests with role and reason.

Auto-detect high-risk roles based on predefined rules.

Approve or reject requests with decision notes.

In-memory H2 database with seed data for quick demo.

Requirements

Java 17+

Maven 3.8+

Setup & Run
# Clone the repo
git clone https://github.com/your-username/iag-mini-no-spring.git
cd iag-mini-no-spring

# Build the jar
mvn clean package

# Run the app
java -jar target/iag-mini-no-spring-1.0-SNAPSHOT-shaded.jar


The server will start on http://localhost:8080.

Usage

Open http://localhost:8080 in a browser to use the UI.

H2 Console (optional): http://localhost:8080/h2

JDBC URL: jdbc:h2:mem:iagdb

Project Structure
model/       # Entity classes
dao/         # Data Access Objects (DB operations)
service/     # Business logic
web/         # HTTP handlers & JSON utils
resources/   # Static HTML & seed SQL
