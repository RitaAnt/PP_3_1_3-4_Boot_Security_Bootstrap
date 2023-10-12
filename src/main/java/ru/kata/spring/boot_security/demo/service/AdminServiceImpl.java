package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AdminServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
    @Override
    public User getUserById(long id) {
        return entityManager.find(User.class, id);
    }
    @Override
    public User getUserByName(String name) {
        return entityManager.createQuery("SELECT username FROM User username WHERE username.username = :name", User.class)
                .setParameter("name", name)
                .getSingleResult();
    }
    @Override
    public List<User> getUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    @Transactional
    @Override
    public void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        entityManager.persist(user);
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        if (userRepository.findById(id).isPresent())
            userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updateUser(long id, User user) {
        User editUser = entityManager.find(User.class, id);
        if (editUser != null) {
            editUser.setUsername(user.getUsername());
            editUser.setEmail(user.getEmail());
            editUser.setRoles(user.getRoles());
            if (!editUser.getPassword().equals(user.getPassword())) {
                editUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            }
        }
    }
}