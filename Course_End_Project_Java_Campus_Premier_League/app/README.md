# Campus Premier League (CPL) - Build & Run Guide

**Course:** Object Oriented Programming through Java (VCE-R25)  
**Batch:** Batch No. 6, Vardhaman College of Engineering  

---

## 1. System Requirements
* **JDK 8 or newer** (JDK 8, 11, 17, and 21 fully verified).
* **MySQL Server 8.0+** (Optional: application includes high-performance in-memory offline fallback).
* **Maven 3.6+** or direct execution of pre-built JAR.

---

## 2. Quick Launch (Windows)
Double-click `run.bat` in this directory or run:
```bash
java -jar target/CampusPremierLeague.jar
```

---

## 3. Build from Source with Maven
```bash
# Clean, compile, run all 25+ unit tests, package shaded executable jar
mvn clean package

# Run the packaged executable jar
java -jar target/CampusPremierLeague.jar

# Or run directly from source tree
mvn exec:java
```

---

## 4. Running the Benchmarking Tool
To execute the 200-match headless plausibility experiment:
```bash
java -cp "target/test-classes:target/classes:lib/mysql-connector-j.jar" com.cpl.tools.Experiment 200
```

---

## 5. MySQL Database Configuration (Optional)
The connection settings are stored in `db.properties` (git-ignored):
```properties
db.host=localhost
db.port=3306
db.name=campus_premier_league
db.user=root
db.password=root123
```
* Under **Database &rarr; Connection Settings**, click **Install Tables** to automatically execute `src/main/resources/sql/cpl_schema.sql` via JDBC.
