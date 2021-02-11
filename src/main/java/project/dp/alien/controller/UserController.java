package project.dp.alien.controller;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import project.dp.alien.dao.UserRepository;
import project.dp.alien.model.User;

import java.text.SimpleDateFormat;
import java.util.*;


@RestController
public class UserController {

    @Autowired
    UserRepository repo;
    boolean transactionBusy = false;
    UUID transactionID = null;

    @PostMapping(value = "/")
    public List<User> home() {
        List<User> users = new ArrayList<>();
        repo.findAll().forEach(users::add);
        return users;
        //return "home.html";
    }

    @PostMapping(value = "/beginTransaction")
    public String beginTransaction() {
        if (!transactionBusy) {
            transactionBusy = true;
            this.transactionID = UUID.randomUUID();
            System.out.println(getCurrentTimeAsString() + "\ttransactionID:\t" + this.transactionID.toString());
            return this.transactionID.toString();
        } else {
            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime >= 400000) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still running");
                } else if (!transactionBusy) {
                    transactionBusy = true;
                    this.transactionID = UUID.randomUUID();
                    String tmp = this.transactionID.toString();
                    System.out.println(getCurrentTimeAsString() + "\ttransactionID:\t" + tmp);
                    return tmp;
                }
            }
        }
    }


    @PostMapping(value = "/getId")
    public User getUser(@RequestParam("id") int id) {
        try {
            User searched_user = repo.findById(id).get();
            System.out.println(getCurrentTimeAsString() + "\tgetId: " + searched_user);
            return searched_user;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/getName")
    public List<String> getUser(@RequestParam("name") String name) {
        try {
            List<String> users = new ArrayList<>();
            for (User user : repo.findAll()) {
                if (user.getName().contains(name)) users.add(user.toString());
            }
            return users;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/add")
    public List<User> addUser(User user, @RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            try {
                if (repo.findById(user.getId()).isEmpty()) {
                    repo.save(user);
                    System.out.println(getCurrentTimeAsString() + "\tadd: " + user);
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is present");
                List<User> users = new ArrayList<>();
                repo.findAll().forEach(users::add);
                return users;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }

    @PostMapping(value = "/update")
    public List<User> updateUser(User user, @RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            try {
                User old_user = repo.findById(user.getId()).get();
                repo.delete(old_user);
                repo.save(user);
                System.out.println(getCurrentTimeAsString() + "\tupdate: " + old_user + " --> " + user);
                List<User> users = new ArrayList<>();
                repo.findAll().forEach(users::add);
                return users;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }

    @PostMapping(value = "/delete")
    public List<User> deleteUser(User user, @RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            try {
                if (repo.findById(user.getId()).isPresent()) {
                    System.out.println(getCurrentTimeAsString() + "\tdelete: " + user);
                    repo.delete(user);
                } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user doesn't exist");

                List<User> users = new ArrayList<>();
                repo.findAll().forEach(users::add);
                return users;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }

    @PostMapping(value = "/commit")
    public ResponseEntity commit(@RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            System.out.println(getCurrentTimeAsString() + "\tcommit");
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }

    @PostMapping(value = "/rollback")
    public ResponseEntity rollback(@RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            System.out.println(getCurrentTimeAsString() + "\trollback");
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }

    @PostMapping(value = "/finalizeTransaction")
    public ResponseEntity finalizeTransaction(@RequestParam("transactionID") String transactionID) {
        if (transactionID.equals(this.transactionID.toString())) {
            transactionBusy = false;
            System.out.println(getCurrentTimeAsString() + "\tfinalizeTransaction");
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other transaction still run");
        }
    }


    String getCurrentTimeAsString() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }


}