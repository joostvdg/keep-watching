# ReactJS Frontend

As the bulk of the application will be in the backend services and the database, the choice was made for a lightweight frontend.

The choice came to using ReactJS build into the Spring Boot backend application itself.

How to set this up, can be found in [this tutorial from Spring.io](https://spring.io/guides/tutorials/react-and-spring-data-rest/). 

## Maven configuration

We make use of the [Frontend Maven Plugin](https://github.com/eirslett/frontend-maven-plugin) to kick off the [Yarn](https://yarnpkg.com/en/) and [WebPack](https://webpack.github.io/) builds.

```xml
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <version>1.4</version>
    <configuration>
        <installDirectory>target</installDirectory>
    </configuration>
    <executions>
        <execution>
            <id>install node and yarn</id>
            <goals>
                <goal>install-node-and-yarn</goal>
            </goals>
            <configuration>
                <nodeVersion>v6.10.3</nodeVersion>
                <yarnVersion>v0.23.4</yarnVersion>
            </configuration>
        </execution>
        <execution>
            <id>yarn install</id>
            <goals>
                <goal>yarn</goal>
            </goals>
        </execution>
        <execution>
            <id>webpack build</id>
            <goals>
                <goal>webpack</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Spring Web configuration

As said, we let Spring Web host the frontend for us, this also removes the nasty CORS problems.

To do this, we have the following configuration.

### Pom.xml

A dependency on spring-boot-starter-thymeleaf.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

### index.html

Thymeleaf will automatically load the index.html in the folder src/main/resources/static.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8" />
        <title>Keep-Watching</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/latest/css/bootstrap.min.css" />
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/latest/css/bootstrap-theme.min.css" />
    </head>
    <body>
        <div id="react" />
        <script src="js/bundle.js" />
    </body>
</html>
```

The build from Yarn and packaging from WebPack will result in the js/bundle.js file.

### App.js

This will make sure that ReactJS will load itself into the page on the ```<div id="react" />``` in the index.html.

```typescript

import React from 'react';
import ReactDOM from 'react-dom';

function App(props) {
    return (<h1>Hi</h1>);
}

const app = <App />;
ReactDOM.render(app, document.getElementById('react'));
```

## Other Configuration

* [WebPack config](https://github.com/joostvdg/keep-watching/blob/master/backend/webpack.config.js)
* [Yarn config](https://github.com/joostvdg/keep-watching/blob/master/backend/package.json)

## React solutions

### Styles

For styles, the choice came to [Bootstrap]().

However, it doesn't always play nice with ReactJS, so we added the following dependencies:

* [react-bootstrap](https://react-bootstrap.github.io/): for easy integration of Bootstrap into ReactJS
* [react-router-bootstrap](https://github.com/react-bootstrap/react-router-bootstrap): for combining the react router with react-bootstrap

### Security

The Spring application has been configured to use Spring Security with OAUTH2 via GitHub/Facebook.

This complicates the calls to the backend, so here's how you can call the backend with the XSRF Token and SessionID intact.

```typescript

import Cookies from 'universal-cookie';
const rest = require('rest');
const mime = require('rest/interceptor/mime');

const cookies = new Cookies();
const xsrfToken = cookies.get('XSRF-TOKEN');

let client = rest.wrap(mime);
client({
    path: '/watchlist/'+id,
    method: 'DELETE',
    headers: {
        'Accept': 'application/json, application/xml, text/plain, text/html, */*',
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': xsrfToken
    },
    credentials: 'same-origin',
    mode: 'cors',
    redirect: 'follow',
});

```
