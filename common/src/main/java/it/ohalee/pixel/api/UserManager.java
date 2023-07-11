package it.ohalee.pixel.api;

import java.util.concurrent.CompletableFuture;

public interface UserManager<T, U, K> {

    U user(K key) throws UserNotFoundException;

    CompletableFuture<U> load(T type, K key) throws UserNotFoundException;

    CompletableFuture<Void> unload(U user);

}
