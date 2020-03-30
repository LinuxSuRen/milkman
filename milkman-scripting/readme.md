# Milkman Scripting plugin

provides post-request script execution via `nashorn` javascript engine (`graal.js` supported but disabled because it is too big in size). 
Provides possibility to pre-load scripts from web, such as chai, via settings.


# Example

```javascript
var url = JSON.parse(milkman.response.body.body).url
milkman.setEnvironmentVariable("test.url", url)
```

example of pre-script:

```javascript
var headerVal = milkman.request.headers.entries[0].value
milkman.toast(headerVal)
```

example of chai script (if you include chai in preload scripts):

```javascript
chai.should();
var body = JSON.parse(mm.response.body.body)
body.should.have.lengthOf(200);
```


# Screenshot

![img](/img/scripting-plugin.PNG)

Preferences:

![img](/img/scripting-preferences.png)


# API

`milkman` (aliased to `mm`) is the only object available. It provides following properties:
 * `request`: an accessor for request-aspects. Every property of this request-object tries to fetch an according aspect. E.g. `request.body` would be the body-aspect of the request-container. (Thats why you have to write `request.body.body`, because the body-aspect stores its content in the `body` variable as well. Might be streamlined in the future)
 * `response`: an accessor for response-aspects. Every property of this response-object tries to fetch an according aspect. E.g. `response.body` would be the body-aspect of the response-container. (Thats why you have to write `response.body.body`, because the body-aspect stores its content in the `body` variable as well. Might be streamlined in the future)
 * `setEnvironmentVariable(String, String)` sets an environment variable in the currently active environment (if there is any activated).
 * `toast(String)` shows a toast at the bottom of the window
 * `log(String)` will output some logs into the result window

`console` works as usual as well
