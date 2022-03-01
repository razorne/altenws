REQUIREMENTS:
- Apache Tomcat (Tested with tomcat 8.5);
- H2 database (wich is attached here to the project);
- JRE 1.8 or above;
- Postman.

TO GET IT RUNNING:
- edit altecore.cfg.xml file in AltenCore project, edit the property hibernate.connection.url to point the location on your local system of the h2 database. You must provide
  an absolute path to the h2 file;
- once you have pointed correctly the h2 file, you should be able to deploy the AltenWS project in tomcat and see the webpage "AltenWS up and running!";
- now you can import the postman project i have attached here and make the requests. You should be able, without altering the headers, to check for availability of the 
  room, reserve it and then cancel the reservation.

HOW DID I DEVELOPED THIS PROJECT:
- In order to spare time, i have avoid logging (a library such as Log4j could be introduced later), and i have used the version of java, spring, hibernate that i know best,
  because it's the one that i work in the company i currently work in. H2 is a database i don't currently work with, but i have work with it in my personal studies. I 
  wrote the code so that it could be testable, and i wrote the unit test that i think can assure the minimum reliability of the service. I have SonarLint installed in my
  IDE in order to guide me with best practices of bug prevention and code readability.
