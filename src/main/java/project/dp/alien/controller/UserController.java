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

import java.util.ArrayList;
import java.util.List;


@RestController
public class UserController
{
    @Autowired
    UserRepository repo;

    @PostMapping(value = "/")
    public List<User> home(){
        List<User> users = new ArrayList<>();
        repo.findAll().forEach(users::add);
        return users;
        //return "home.html";
    }

    @PostMapping(value = "/getId")
    public User getUser(@RequestParam("id") int id){
        try{
            User searched_user = repo.findById(id).get();
            System.out.println("getId: " + searched_user);
            return searched_user;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/getName")
    public List<String> getUser(@RequestParam("name") String name){
        try{
            List<String> users = new ArrayList<>();
            for(User user : repo.findAll()) {
                if(user.getName().contains(name)) users.add(user.toString());
            }
            return users;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/add")
    public List<User> addUser(User user){
        try{
            if(repo.findById(user.getId()).isEmpty()) {
                repo.save(user);
                System.out.println("add: " + user);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user is present");
            List<User> users = new ArrayList<>();
            repo.findAll().forEach(users::add);
            return users;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/update")
    public List<User> updateUser(User user){
        try{
            User old_user = repo.findById(user.getId()).get();
            repo.delete(old_user);
            repo.save(user);
            System.out.println("update: " + old_user + " --> " + user);
            List<User> users = new ArrayList<>();
            repo.findAll().forEach(users::add);
            return users;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/delete")
    public List<User> deleteUser(User user){
        try{
            if(repo.findById(user.getId()).isPresent()) {
                System.out.println("delete: " + user);
                repo.delete(user);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user doesn't exist");

            List<User> users = new ArrayList<>();
            repo.findAll().forEach(users::add);
            return users;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
        }
    }

    @PostMapping(value = "/commit")
    public ResponseEntity commit() {
        System.out.println("commit");
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/rollback")
    public ResponseEntity rollback() {
        System.out.println("rollback");
        return new ResponseEntity(HttpStatus.OK);
    }

//    @RequestMapping(value = "/get")
//    public String getUser(User user){//(PathVariable("id") int id){
//        try{
//            User searched_user = repo.findById(user.getId()).get();
//            System.out.println(searched_user);
//            //return searched_user.toString();
//        } catch(Exception e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
//        }
//        return "home.html";
//    }
//
//    @RequestMapping("/add")
//    public String addUser(User user){
//        try{
//            repo.save(user);
//        } catch(Exception e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
//        }
//        return "home.html";
//    }
//
//    @PostMapping(value = "/add2")
//    public List<User> addUser2(User user){
//        try{
//            repo.save(user);
//        } catch(Exception e){
//        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed adding user");
//    }
//    List<User> users = new ArrayList<>();
//        repo.findAll().forEach(users::add);
//        return users;
//    }
//
//    @RequestMapping("/delete")
//    public String deleteUser(User user){
//        try{
//            User old_user = repo.findById(user.getId()).get();
//            repo.delete(old_user);
//        } catch(Exception e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't delete this user");
//        }
//        return "home.html";
//    }
//
//    @RequestMapping("/update")
//    public String updateUser(User user){
//        try{
//            User old_user = repo.findById(user.getId()).get();
//            repo.delete(old_user);
//            repo.save(user);
//        } catch(Exception e){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can't update this user");
//        }
//        return "home.html";
//    }
}