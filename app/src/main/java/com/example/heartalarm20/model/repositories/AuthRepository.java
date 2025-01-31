package com.example.heartalarm20.model.repositories;

public interface AuthRepository {

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(T error);
    }
}
