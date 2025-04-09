package ru.kata.spring.boot_security.demo.repository;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByUsernameWithRoles(String username) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username",
                            User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByIdWithRoles(Long id) {
        try {
            User user = entityManager.createQuery(
                            "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id",
                            User.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAllWithRoles() {
        return entityManager.createQuery(
                        "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
                        User.class)
                .getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                        Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                        Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public void save(User user) {
        entityManager.persist(user);
    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

    @Override
    public Set<Long> findRoleIdsByUserId(Long userId) {
        return Set.copyOf(entityManager.createQuery(
                        "SELECT r.id FROM User u JOIN u.roles r WHERE u.id = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getResultList());
    }
}