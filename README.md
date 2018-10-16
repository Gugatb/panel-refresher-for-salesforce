
Proposal
-----------------------------------------------------------------------------------------
1. Create application to access the Salesforce report panel.

2. Periodically refresh the panel in minutes.

3. Alterne a visualização do navegador.

Technologies used
-----------------------------------------------------------------------------------------
Java 8, AutoIt 3, Selenium 2.52

Settings and notes
-----------------------------------------------------------------------------------------
Configure the data.ini file:  

Diretorio = Chrome driver location  
Fullscreen = Fullscreen mode  
Senha = User password - Optional (Salesforce)  
Usuario = Username - Optional (Salesforce)  
Versao = Version - Classic or Lightning  

Url = Url to panel page  
Foco = Seconds to alternate the page  
Minutos = Minutes to refresh the panel  
Zoom = Zoom to page (0.5, 0.8, 1.0, 1.5, etc)  

Execute the Refresher.au3 as administrator.
