Instructions to use the program
-------------------------------

* Make sure you have Maven and Java Installed

* Go the directory where pom.xml file is located and use below options

  * Using spring-boot maven plugin
  
        mvn spring-boot:run

  * Using packaged jar
    
        mvn clean package
        
        java -jar target/fxcalculator-1.0-SNAPSHOT.jar
       
  * Test coverage
        
        mvn test
        
        View report at {project.home}/target/site/jacoco/index.html



Sample console output (Use q or Q to exit program)
--------------------------------------------------

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
