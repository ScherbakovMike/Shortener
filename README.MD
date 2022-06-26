## URL Shortener

### Introduction

This application has been implemented for several tasks:
1. Transforming long URL's like "http://mikescherbakov.ru......./tests/fsdfsdf" to the short form as "/l/B"
2. Processing the web redirection on using short links.
3. Collecting the metrics of using the redirect service.

### Using

Run the application using jar file or from the IDE using command "__mvn spring-boot:run__", or use pre-deployed service on URL:

http://mikescherbakov.ddns.net:8233/

#### Generating the short URL
Send the POST-request to

http://mikescherbakov.ddns.net:8233/generate

with body:
__{
"original": "http://yandex.ru"
}__

#### Redirecting

Use the link, for ex.:

http://mikescherbakov.ddns.net:8233/l/B

#### Metrics of using

To taking the metrics, use short form:

http://mikescherbakov.ddns.net:8233/stats/l/B

or detailed paginated form:

http://mikescherbakov.ddns.net:8233/stats?page=0&count=100

### Contacts

Mike Scherbakov

email: scherbakov@gmail.com

tel: +79165303619