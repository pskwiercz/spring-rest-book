package com.pskwiercz.restapp.reactive;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.ipc.netty.http.server.HttpServer;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

public class Server {

    public static final String HOST = "localhost";
    public static final int PORT = 8081;

    public static void main(String[] args) throws InterruptedException, IOException {
        Server server = new Server();
        server.startReactorServer();
        System.out.println("Press ENTER to exit.");
        System.in.read();
    }

    public void startReactorServer() throws InterruptedException {
        RouterFunction<ServerResponse> route = routingFunction();
        HttpHandler httpHandler = toHttpHandler(route);
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer server = HttpServer.create(HOST, PORT);
        server.newHandler(adapter).block();
    }

    public RouterFunction<ServerResponse> routingFunction() {
        UserRepository repository = new UserRepositoryImpl();
        UserHandler handler = new UserHandler(repository);
        return nest(
                path("/user"),
                nest(
                        accept(APPLICATION_JSON),
                        route(GET("/"), handler::getAllUsers)
                )
                .andRoute(GET("/{id}"), handler::getUser)
                .andRoute(POST("/").and(contentType(APPLICATION_JSON)), handler::createUser)
                .andRoute(PUT("/").and(contentType(APPLICATION_JSON)), handler::updateUser)
                .andRoute(DELETE("/{id}").and(contentType(APPLICATION_JSON)), handler::deleteUser)
        );
    }
}
