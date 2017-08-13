# Spring Security

The current implementation follows the [Spring Boot oauth2 tutorial](https://spring.io/guides/tutorials/spring-boot-oauth2/) and connects to OAUTH2 from GitHub and Facebook.

## Configuration

The configuration for the OAUTH2 authentication is present in two files and the respective OAUTH2 services.

* Application.java (src/main/java/com/github.com/joostvdg.keepwatching)
* application.yml (src/main/resources) 
* [Facebook Developer Portal](https://developers.facebook.com/)
* [GitHub OAUTH2 introduction](https://developer.github.com/apps/building-integrations/setting-up-and-registering-oauth-apps/)

### application.yml

```yaml
facebook:
  client:
    clientId: XXXXXXXXXXXX
    clientSecret: XXXXXXXXXXXX
    accessTokenUri: https://graph.facebook.com/oauth/access_token
    userAuthorizationUri: https://www.facebook.com/dialog/oauth
    tokenName: oauth_token
    authenticationScheme: query
    clientAuthenticationScheme: form
  resource:
    userInfoUri: https://graph.facebook.com/me

github:
  client:
    clientId: XXXXXXXXXXXX
    clientSecret: XXXXXXXXXXXX
    accessTokenUri: https://github.com/login/oauth/access_token
    userAuthorizationUri: https://github.com/login/oauth/authorize
    clientAuthenticationScheme: form
  resource:
    userInfoUri: https://api.github.com/user

```
