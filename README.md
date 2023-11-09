# Chester Hunts KtorRestAPI

<img src="https://ktor.io/docs/images/ktor_logo_dark.svg" width="128"/>
<img src="https://jwt.io/img/logo-asset.svg" width="128"/>


A REST API built using Ktor for an Android stamp colection game.

<h2>Components and Dependencies</h2>

This API currently includes four major plugins :

<ul>
  <li>Application</li>
  <li>Routing</li>
  <li>Monitoring</li>
  <li>Security</li>
  <li>Serialization</li>
</ul>

Located in the ../plugins folder

<b>Server Application</b>

<b>Routing</b>
 
This Ktor Server application utlizes the netty engine version 2.0.0

This is currently used as testing and production server.

<b>Routing</b>

<ul>
  <li>Consits of several authentication and non authentication routes that serves players requests asyncronously.</li>
  <li>This plugin also starts the mail server used to handle user account-related requests.</li>
  <li> User and game-related information are accessed from the MySQL database using queries located in the MySQL repository </li>
  <li>JWT access and refresh tokens time-related values can be found in the Constant file and token generation information is available in the relevant library documentation.</li>
</ul>

<b>Monitoring</b>

<ul>
  <li>Due to the small scale nature of the project monitoring, all route info is shown in the console</li>
  <li>[ ] Need to change the logging level of Simple Kotlin Mail Server</li>
</ul>

<b>Security</b>

<ul>
  <li>Security "secret" reference is located in the application.conf file. Please note ktorKey the reference for "secret" must be added as an environmnet variable of your system inorder for server to work</li>
  <li>JWT structure and port info. is also in the applicaiton.conf file</li>
  <li>[ ] Need to change the level logging for Simple Kotlin Mail Server</li>
</ul>

<b>Serialization</b>

<ul>
  <li>Handled using the GSON library</li>
  <li>[ ] Implement RASP for added protection </li>
  <li>[ ] Enhance Security Testing </li>
</ul>


<h2>Misc TODOS</h2>


 <table>
  <tr>
    <th>Todo Items</th>
    <th>Priority</th>
    <th>Completion Status</th>
  </tr>
  <tr>
    <td>Load Testing of the Simple Kotlin Mail Server</td>
    <td>P3</td>
    <td>not completed</td>
  </tr>
  <tr>
    <td>Reset Password Timeout Testing</td>
     <td>P2</td>
    <td>not completed</td>
  </tr>
   <tr>
    <td>Reformat database entity mapping to with new normalized table</td>
    <td>P1</td>
     <td>not completed</td>
  </tr>
   <tr>
     <td>Stamp Table needs normalization</td>
      <td>P1</td>
     <td>not completed</td>
  </tr>
</table> 











