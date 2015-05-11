
Mobile payments with Titanium, Authorize.net, Spring Boot and Cloud Foundry
==================================================================================
This repo contains two sample applications (mobile, server) that together demonstrate how to submit an Authorize.net payment from a mobile app using Authorize.net's [DPM API (Direct Post Method)](http://developer.authorize.net/api/dpm/) and [AIM API (Advanced Integration Method)](http://developer.authorize.net/api/aim/).

The mobile app is built with [Titanium (Alloy)](http://www.appcelerator.com/titanium/alloy/) and the server side application is a standalone microservice built with [Spring Boot](https://github.com/spring-projects/spring-boot).


Getting Started
===============

Before running the applications, fill in all the areas marked with ```// TODO``` in both the ```ti``` and ```boot``` apps; i.e, the host name and the Authorize.net credentials. In addition, for the boot app, fill in the needed properties in both ```application.properties``` and ```application-cloud.properties```


### Running the Titanium App
To start the Titanium mobile app:

- cd into the ```ti``` directory
- Build and launch the app with either the [Ti CLI](http://docs.appcelerator.com/titanium/3.0/#!/guide/Titanium_Command-Line_Interface_Reference) or Ti Studio for the platform of choice (iOS/Android).

The ti ```<sdk-version>``` is set up for 3.5.1.GA but should run on 3.2.x or higher.


### Building the Spring Boot Microservice

1. To Start the Spring Boot app (_Requires Java 7 or 8_)

  - cd into the ```boot``` directory

  - Run ```./gradlew build``` to download the dependencies and build the app (the executable jar will be output to ```build/libs```)

  - Runing ```./gradlew test``` will run the ```ApiSpec.groovy``` [Spock](https://github.com/spockframework/spock) specification. The spec will verify that all the endpoints are in working order

### Deploying the Spring Boot Microservice

**Run on Cloud Foundry**

Using [Cloud Foundry](http://cloudfoundry.org/) is great way to test this app since the response relay endpoint required for Authorize.net will be instantly exposed to the Internet over https.

Included in the app is a configured ```manifest.yml``` file for easy deployment to a Cloud Foundry instance. Assuming a public instance of [Pivotal Web Services account](http://run.pivotal.io) is available and the [Cloud Foundry CLI](https://github.com/cloudfoundry/cli) has been installed, deployment is as simple as logging into your account from the CLI and running ```cf push``` from the project root.

Note: The ```host``` name in the ```manifest.yml``` has been set up as ```authnet-ti-boot-${random-word}```. This is to allow multiple users to deploy as is without conflict.

To view the active logs, from the CLI use ```cf logs authnet-ti-boot-{your random name}``` to tail the running app log.

**Run standalone on a Linux server**

The ```start.sh``` is set up to run the application on Linux and assumes the ```boot-0.0.1.jar``` is in the same dir as the start script. Just edit the ```start.sh``` script to add the Java path. (*currently set to run on port 8282*


**Run in Docker container**

All the docker related config is in ```boot/docker``` directory.

 - cd into the ```boot/docker``` directory
 - run ```./set-env.sh```
 - run ```./build.sh``` (builds the Docker image)
 - run ```./run-service.sh```

 The ```Dockerfile``` is currently set up to use the java:oracle-java7 image.


**System Check**

The Spring Boot [actuator](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready) has been included in the app which provides several [endpoints](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready-endpoints) for checking health and configs of the running system. Example of hitting the URI: ```/health```

``` json
{
"status": "UP"
}
```

How it Works
============

**Obtain a Fingerprint and send the AUTH_ONLY request**

From the mobile app (handled by ```index.js```), submitting the form will first call the Spring Boot service endpoint ```/boot/authnet/access``` in the ```AccessController``` to obtain a [Unique Transaction Fingerprint](http://developer.authorize.net/guides/SIM/wwhelp/wwhimpl/js/html/wwhelp.htm#href=SIM_Submitting_transactions.06.3.html) (returned as a JSON payload). The Fingerprint is then used to send a direct POST (AUTH_ONLY) to Authorize.net.

**Handling the AUTH_ONLY response**

Authorize.net will then POST back the response to the ```/boot/relaycapture/relay``` endpoint in the ```RelayCaptureController```. **This endoint needs to be Internet exposed**. Contained in the response will be the transaction ID and and status code from the auth. The ```RelayCaptureController``` will send the response (as JSON) back to the Authorize.net gateway. This response is then returned to the mobile app.

**Important**: The ```/boot/relaycapture/relay``` endpoint will need to be registered as the _Relay Response URL_ in the Authorize.net developer sandbox, under Account > Settings > Relay Response.

**Performing the PRIOR_AUTH_CAPTURE**

Pressing the _Capture_ button from the mobile app (handled by ```capture.js```) sends a POST to the Spring Boot service ```/boot/relaycapture/capture``` passing the transaction ID and amount as a JSON payload.

The ```/boot/relaycapture/capture``` endpoint uses the AIM API to send the transaction ID and amount as a PRIOR_AUTH_CAPTURE transaction to Authorize.net. The response, containing the status received from the AIM transaction is then sent back to the mobile device completing the payment process.
