package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = Router.router(vertx);
        JsonObject jo = new JsonObject();

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/:language").handler(this::hello);

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    public void hello(RoutingContext routingContext) {
        String language = routingContext.request().getParam("language");
        JsonObject body = new JsonObject();
        body.put("language", language);

        switch (language){
            case "spanish":
                body.put("title", "Hola");
                break;
            case "english":
                body.put("title", "Hi");
                break;
            default:
                body.put("title", "Not found");
                break;
        }

        HttpServerResponse response = routingContext.response();

        if("Not found".equals(body.getValue("title"))) {
            response.setStatusCode(500);
        }

        response
                .putHeader("content-type", "application/json")
                .end(body.toString());
    }
}
