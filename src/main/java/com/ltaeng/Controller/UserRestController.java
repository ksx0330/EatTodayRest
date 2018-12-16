package com.ltaeng.Controller;

import com.ltaeng.Domain.User;
import com.ltaeng.Repository.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserRestController {
    @Inject
    private UserMapper userMapper;

    @Transactional
    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> signup(
            @RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("password") String password) {
        User user = new User();
        user.setUniqueCode(UUID.randomUUID().toString().replace("-", ""));
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        userMapper.insert(user);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<User> login(
            @RequestParam("email") String email, @RequestParam("password") String password) {

        User login = userMapper.login(email, password);
        if (login == null)
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<User>(login, HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/edit/{uniqueCode}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<Void> edit(
            @PathVariable("uniqueCode") String uniqueCode, @RequestBody User user) {
        User tmp = userMapper.findByUniqueCode("uniqueCode");
        tmp.setName(user.getName());
        tmp.setEmail(user.getEmail());
        tmp.setPassword(user.getPassword());

        userMapper.update(tmp);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value = "/leave/{uniqueCode}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<Void> leave(
            @PathVariable("uniqueCode") String uniqueCode) {

        userMapper.deleteByUniqueCode(uniqueCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
