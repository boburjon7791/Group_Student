package com.example.demo.controllers;

import com.example.demo.dto.GroupDto;
import com.example.demo.entities.Groups;
import com.example.demo.exceptions.BadParamException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositories.GroupRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;


@RestController
@Slf4j
@RequestMapping("/api.group")
public class GroupController {
    private final GroupRepository groupRepository;

    public GroupController(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PostMapping("/create")
    public Groups create(@RequestBody GroupDto groupDto,HttpServletRequest req ,HttpServletResponse res){
        Groups groups = Groups.builder()
                .name(groupDto.name)
                .build();
        Groups save = groupRepository.save(groups);
        addCookie(req,res,"group_id",groups.getId());
        return save;
    }
    @GetMapping("/get")
    public ResponseEntity<Groups> get(HttpServletResponse res,HttpServletRequest req,@RequestParam(name = "id") Integer id){
        System.out.println("id = " + id);
        Groups groups = groupRepository.findById(id).orElseThrow(NotFoundException::new);
        if (groups.getId()==null) {
           return new ResponseEntity<>(groups, HttpStatus.NOT_FOUND);
        }
        addCookie(req,res,"group_id",groups.getId());
        return new ResponseEntity<>(groups,HttpStatus.FOUND);
    }
    @GetMapping("/get-with-name")
    public ResponseEntity<Groups> get(HttpServletResponse res,HttpServletRequest req,@RequestParam(name = "name") String name){
        Groups groups = groupRepository.findByName(name);
        if (groups.getId()==null) {
            return new ResponseEntity<>(groups, HttpStatus.NOT_FOUND);
        }
        addCookie(req,res,"group_id",groups.getId());
        return new ResponseEntity<>(groups,HttpStatus.FOUND);
    }
    @PutMapping("/edit")
    public Groups edit(@RequestBody GroupDto groupDto,HttpServletRequest req,HttpServletResponse res,
                       @CookieValue(name = "group_id")Long group_id){
        Groups groups = groupRepository.getGroupBy(group_id);
        groups.setName(groupDto.name);
        addCookie(req,res,"group_id",groups.getId());
        Groups save = groupRepository.save(groups);
        GroupLogger.updateEntity(save);
        return save;
    }
    @DeleteMapping("/delete")
    public void delete(HttpServletResponse res,HttpServletRequest req, @RequestParam(required = false,name = "id") Integer id){
        Cookie[] cookies = req.getCookies();
        Integer groupId=null;
        if (!Objects.isNull(cookies)) {
            Optional<Cookie> first = Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals("group_id"))
                    .findFirst();
            if (first.isPresent()) {
                groupId= Integer.valueOf(first.get().getValue());
            }
        }
        Integer s_id=Objects.requireNonNullElse(id,groupId);
        Optional<Groups> optionalGroups = groupRepository.findById(s_id);
        System.out.println("groupId = " + groupId);
        groupRepository.delete(optionalGroups.orElseThrow(NotFoundException::new));
        GroupLogger.deleteEntity(optionalGroups.get());
        deleteCookie(req,res,"group_id");
    }
    @DeleteMapping("/delete-with-name")
    public void delete(@RequestParam(name = "name") String name,HttpServletRequest req,HttpServletResponse res){
        Groups byName = groupRepository.findByName(name);
        groupRepository.delete(byName);
        GroupLogger.deleteEntity(byName);
        deleteCookie(req,res,"group_id");
    }
    protected static void addCookie(HttpServletRequest req,HttpServletResponse res,String name,Object value){
        Cookie[] array = req.getCookies();
        Cookie cookie=null;
        if (!Objects.isNull(array)) {
            Optional<Cookie> first = Arrays.stream(array)
                    .filter(cookie1 -> cookie1.getName().equals(name))
                    .findFirst();
            if (first.isPresent() && first.get().getValue()!=null && first.get().getValue().equals(value)) {
                cookie = first.get();
                cookie.setMaxAge(24*60*60);
                res.addCookie(cookie);
            }else {
                cookie = new Cookie(name, String.valueOf(value));
                cookie.setMaxAge(24*60*60);
                cookie.setSecure(false);
                res.addCookie(cookie);
            }
        }else {
            cookie = new Cookie(name, value.toString());
            cookie.setMaxAge(24*60*60);
            res.addCookie(cookie);
        }

    }
    protected static void deleteCookie(HttpServletRequest req,HttpServletResponse res,String name){
        Cookie[] array = req.getCookies();
        if (!Objects.isNull(array)) {
            Optional<Cookie> first = Arrays.stream(array)
                    .filter(cookie -> cookie.getName().equals(name))
                    .findFirst();
            if (first.isPresent()) {
                Cookie cookie = first.get();
                cookie.setMaxAge(-1);
                res.addCookie(cookie);
            }
        }
    }
}
