###############################
INSTRUCTIONS TO USE THE PROGRAM
###############################

Maven and related plugins are required to run the program directly without packaging.

Make sure Maven and Java paths are correctly set
Go the directory where pom.xml file is located and use below options

1. Run tests
mvn test

2. Run tests and redirect output to logs
mvn test --log-file fxCalculatorTests.log

NOTE: After running tests, Test code coverage report can be viewed at
 {project.home}/target/site/jacoco/index.html

3. To run the program using maven, go the pom.xml containing directory, and execute below command
mvn clean install exec:java -Dexec.mainClass="com.anz.interview.fxCalculator.app.main" -Dexec.classpathScope=test -e

4. To build the program
mvn clean package


#####################################################
COMMAND EXAMPLES -- TO EXIT THE PROGRAM ENTER q or Q
#####################################################

%> AUD 100.00 in USD
AUD 100.00 = USD 83.71
%> AUD 100.00 in AUD
AUD 100.00 = AUD 100.00
%> AUD 100.00 in DKK
AUD 100.00 = DKK 505.76
%> JPY 100 in USD
JPY 100 = USD 0.83
%> KRW 1000.00 in FJD
Unable to find rate for KRW/FJD

%> q
Process finished with exit code 0