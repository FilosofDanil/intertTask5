package com.example.demo.services;

import java.util.List;

/**
 * Interface for basic CRUD operations.
 *
 * @param <T> The type of object this service will operate on
 */
public interface CRUDService<T> {
    /**
     * Retrieves all objects of type T.
     *
     * @return A list of all objects of type T
     */
    List<T> getAll();

    /**
     * Retrieves an object by its ID.
     *
     * @param id The ID of the object to retrieve
     * @return The object with the given ID, or null if not found
     */
    T getById(Long id);

    /**
     * Creates a new object.
     *
     * @param t The object to create
     * @return The created object
     */
    T create(T t);

    /**
     * Updates an existing object.
     *
     * @param t  The updated object
     * @param id The ID of the object to update
     */
    void update(T t, Long id);

    /**
     * Deletes an object by its ID.
     *
     * @param id The ID of the object to delete
     */
    void delete(Long id);
}
