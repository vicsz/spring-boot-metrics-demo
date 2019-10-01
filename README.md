# Spring Boot Observability with PCF Metrics Demo

Application demonstrating Logging, Tracing, and Metrics functionality with Spring Boot 2.X (with Micrometer) and functionality of the "PCF Metrics" Tile in Pivotal Cloud Foundry.

Centralized logging / metrics / and logging functionality is provided out-the-box with the PCF platform with a default storage time of 2 weeks.

Third-party platform ingestion (i.e. Splunk, Datadog, Promethus) can be added as well for more advanced use-cases.

## Demo Requirements

### 1 - PCF 2.4 or higher with PCF Metrics Tile installed (available on PWS)

### 2 - CF CLI installed locally

## Demo Steps

### 1 - Build and Deployment

Clone to Source Code Repo

```sh
git clone https://github.com/vicsz/spring-boot-metrics-demo.git
```

```sh
cd spring-boot-metrics-demo
```

Build the application (via Gradle)

```sh
./gradlew
```

or build the application (via Maven)
```sh
./mvnw package
```

Deploy to PCF using the CLI

```sh
cf push
```

### 2 - Setup

#### 2.1 - Logging Setup

No setup required, PCF automatically takes all STDIO / STERR output from deployed applications.

#### 2.2 - Tracing Setup

No setup required, PCF automatically detected tracing information provided from Spring Cloud Sleuth dependency.

> Discussion Item - Automatic Trace Header Injection from Spring Cloud Sleuth Dependency (see build.gradle, or pom.xml)

#### 2.3 - Metrics Setup

##### Ensure Metric Registrar CLI plugin is installed

```sh
cf plugins
```

If *metric-registrar* does not exist run the following command:

```sh
cf install-plugin -r CF-Community "metric-registrar"
```

> Remember to replace APP-NAME with your application name.

##### Register your Metrics endpoint with PCF

```sh
cf register-metrics-endpoint metrics-demo /actuator/prometheus
``` 

> Remember to replace APP-NAME with your application name.



### 3 - Demo


PCF Metrics GUI is PCF AppsMan application view under the "View in PCF Metrics" button.

Application functionality is available from the default application path ( i.e. the application index page ).

> Note - On PWS (Pivotal Web Services) there is a Metrics ingestion delay of ~10 minutes.

#### 3.1 - PCF Metrics Logging

Demo Logging and Search in PCF Metrics, including filtering Application logs, searching and selecting time intervals.

Create applications logs -- via provided web GUI (Cause JVM or JS Error).

Also demo CLI access to centralized logging.

```sh
cf logs metrics-demo
```



#### 3.2 - Tracing Demo

Discuss the value of "Correlation IDs" , especially with regards to MicroService designs.

Invoke a traced calling -- via provided web GUI (Simple Trace Call Button).

Locate it in the PCF Metrics Log view, and view it in the Trace Explorer.

<img src="img/viewtrace.png" width="750">

PCF Metrics Trace ingestion may take up to a few minutes.

Note that Logs will be aggregated across Applications as well (assuming shared TraceId).

<img src="img/trace.png" width="750">

#### 3.3 - Metrics Demo

Demo Application Metrics including the built-in ones listed with the /actuator/metrics endpoint. As well as the custom ones used in the MetricsController.

Demo creation of Chart in PCF Metrics using custom application data.

You can demo :

1) automatically added metrics from Micrometer
2) auto generating application metrics - such as the *purchase* metric tracking random purchase amounts from the application.
3) manually invoked metrics from the application GUI using the : Run Timed Method, Add to Distribution Summary buttons.

Available Micrometer metrics are also available to view at the /actuator/metrics endpoint of your application.

<img src="img/metrics.png" width="750">

> Discussion Item - Note Counter Metrics Types are currently NOT supported in PCF Metrics.



### 4 - Alerting (Optional)

#### 4.1 - Alerting Setup

Create a Slack Incoming WebHook URL

One can be created by logging into your Slack account at www.slack.com, browsing the *App Directory* for *Incoming WebHooks* and adding your own configuration.

(App Directory Link is available on home page footer of Slack under Resources).

<img src="img/appdirectory.png" width="750">

<img src="img/webhook.png" width="750">

<img src="img/webhookurl.png" width="750">

You should then be able to send Slack messages to yourself by *posting* to that URL.

Example: (note INSERT_YOUR_WEB_HOOK_URL -- update this with your generated "Webhook URL")

```sh
curl -s -d "payload={\"text\":\"Test Message\"}" INSERT_YOUR_WEB_HOOK_URL_HERE
```

To demo the Application Error Level Log Alerting with deployed PCF apps, make sure your PCF app instance has the *SLACK_INCOMING_WEB_HOOK* environment variable set to your URL.

To add this value using the CLI (update INSERT_YOUR_WEB_HOOK_URL_HERE accordingly)

```sh
cf set-env metrics-demo SLACK_INCOMING_WEB_HOOK INSERT_YOUR_WEB_HOOK_URL_HERE
```

You will need to re-stage for the changes to take effect:

```sh
cf restage metrics-demo
```

For local testing you will need to set your SLACK_INCOMING_WEB_HOOK environment variable accordingly.

```sh
export SLACK_INCOMING_WEB_HOOK=https://hooks.slack.com/services/SOME_CORRECT_VALUE

```

#### 4.2 - Alerting Demo

Note SLACK_INCOMING_WEB_HOOK environment variable needs to be set -- either in PCF or locally.

> TIP - Add Logging Error Level Alerts to your Applications for quick notification of Application Issues and reduction in *Mean-Time-to-Repair* (MTTR).

##### Server-Side (JVM)

Demo Server-Side Alerting (JVM), by causing a Server-Side Application Error / Exception (button).

##### Client-Side (JS)

Demo Client-Side Alerting (JS), by causing a Client-Side Error / Exception (button).

Note that we are persisting the UserAgent -- important in helping isolate "page-snap" issues.



### 5 - Additional Demo Notes

#### Exposed Actuator Endpoints

Note that most actuator endpoints are restricted / locked down by default , hence the change in the *application.properties* file to expose all of them (not recommended in production)

```properties
management.endpoints.web.exposure.include=*
```
#### Logfile Actuator Endpoint Requirements

For the logfile endpoint to be enabled, to application needs to be configured to save logs to a local flat file (not a default).

#### VCAP environment variable usage in logback.xml

Note the VCAP environment variable usage in logback.xml to include PCF information in Slack Alerts.

#### Custom CONSOLE_LOG_PATTERN in logback.xml

For Log messages to correlate to Trace / Span Id's,  these 2 pieces of information need to be included in log entries.

#### Route usage (in TraceController) via VCAP variables

For demo purposes, the Demo is calling itself (via app route) , use of localhost:8080 will prevent Tracing Information from showing up as it's captured at the route request level.

#### WebClient Bean Annotation

WebClient needs to be annotated with Bean annotation for Spring Cloud Sleuth to correctly "hook in" and inject Trace information.

#### Tracing Spring Cloud Sleuth Requirements

The org.springframework.cloud:spring-cloud-starter-sleuth dependency is required for injection of required Tracing information.

#### Metrics Label / Tag Cardinality 

High cardinality label / tag values (i.e. unique guids, user data such as emails, etc) for metrics implementations are highly discouraged, and have the potential to overwhelm time-series databases such as Datadog or Promethues. 

### 6 - 3rd-Party Metrics -- Humio Integration Instructions (Optional)

As an alternate target for Metrics, you can also use alternate Metrics Platform Solutions. One such / easy to setup solution is Humio.

Setup a Free Humio Cloud account at : https://humio.com

Add the Humio Micrometer registry to your build dependencies. 

```gradle
    compile('io.micrometer:micrometer-registry-humio:latest.release')
```

In your sandbox repo settings, create and get an Ingest Token. 

Update your application properties with the Humio ingest token:

```properties
    management.metrics.export.humio.api-token=YOUR_TOKEN
```

<img src="img/humio.png" width="800">

Sample Humio Queries: 

Timechart example:

__name=purchase | timechart(function=avg(sum))__

Timechart example split by product_name (label / tag):

__name=purchase | timechart(product_name, function=avg(sum))__
