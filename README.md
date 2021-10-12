<p align="center">
  <img width="770" height="350" src="https://user-images.githubusercontent.com/79100490/136980997-9806f172-4fce-4413-b070-8c48445f2ffc.PNG">
</p>


## Description

This is a multi-platform project, mainly written in Java.
The project's purpose is to generate time tables according to specific requests from the user.
The project uses a modifiable Evolutionary Algorithm[1] in order to find the best possible setting of the time table.

Enjoy!


## Technology Stack

Console application - Written entirely in Java.
Desktop application - Written in Java, as well as JavaFX SDK.
Web Application - Written in Java and Javascript. HTML and CSS were akso used.


## How To Run

Console application - 1. Download the "Console Application" folder.
                      2. Double Click "RunTimeTableEvolutionaryAlgorithm.bat".
                      
Desktop application - 1. Download the "Desktop Application" folder.
                      2. Double Click "RunEvolutionaryTimeTableJavaFXProgram.bat".
                      (Make sure you have Java installed on your machine)
                      
Web application -     1. Download the "Web Application" folder.
                      2. Extract "apache-tomcat-8.5.72.zip".[2]
                      3. Drag and Drop "ETT.war" from the "Run" folder into the "webapps" folder under the extracted Tomcat folder.
                      4. Using the command prompt, enter the "bin" folder under the extracted Tomcat folder.
                      5. Run "startup.bat".
                      6. In your browser, go to "http://localhost:8080/ETT" and enjoy.
                     (7. To open the desktop application that uses the same server, double click "Run JavaFX Program.bat".)
                     
Note: Follow each README file attached to each folder for more instructions and details.


## Contribution

Feel free to add your contributions, and ask for more features.

Contributors:
- Daniel Malky

Special Thanks:
- Maxim Bunkov => Designer of the logo (Github account: https://github.com/killegopink)


[1]: https://en.wikipedia.org/wiki/Evolutionary_algorithm
[2]: http://tomcat.apache.org/

Made by Daniel Malky
