package project.dp.alien.dao;

import org.springframework.data.repository.CrudRepository;
import project.dp.alien.model.User;


public interface UserRepository extends CrudRepository<User,Integer> {

}