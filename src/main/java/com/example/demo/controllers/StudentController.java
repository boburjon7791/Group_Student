package com.example.demo.controllers;

import com.example.demo.dto.StudentDto;
import com.example.demo.entities.Groups;
import com.example.demo.entities.Student;
import com.example.demo.exceptions.BadParamException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositories.GroupRepository;
import com.example.demo.repositories.StudentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@Slf4j
@RequestMapping("/api.student")
public class StudentController {
    /*
     * task ni oxirida aytilgan kutubxona orqali log bilan ishlay olmadim
     * */

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;


    public StudentController(StudentRepository studentRepository, GroupRepository groupRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
    }
    @GetMapping("/all")
    public Page<Student> students(@RequestParam(name = "size",required = false,defaultValue = "10") Integer size, @RequestParam(name = "page",required = false,defaultValue = "0") Integer page){
        return studentRepository.findAll(PageRequest.of(page, size));
    }
    @PostMapping("/create")
      public ResponseEntity<StudentDto> create(@RequestBody StudentDto studentDto, HttpServletRequest req, HttpServletResponse res){
        System.out.println("studentDto.gender = " + studentDto.gender);
        Optional<Student.Gender> optionalGender = Arrays.stream(Student.Gender.values())
                  .filter(gender -> gender.name().equals(studentDto.gender))
                  .findFirst();
        Student.Gender gender=optionalGender.orElse(null);
          if (Objects.isNull(gender)) {
              throw new BadParamException();
          }
        Groups groups = groupRepository.findByName(studentDto.group_name);
        Student student = Student.builder()
                .name(studentDto.name)
                .age(studentDto.age)
                .groups(groups)
                .birthDate((studentDto.birthdate))
                .gender(gender.name()).build();
        if (Objects.isNull(groups)) {
            throw new BadParamException();
        }
        groups.addStudent(student);
        Groups save1 = groupRepository.save(groups);
        Student save = studentRepository.save(student);
        GroupController.addCookie(req,res,"student_id",save.getId());
        GroupController.addCookie(req,res,"group_id",save1.getId());
        studentDto.id = save.getId();
        return new ResponseEntity<>(studentDto, HttpStatus.CREATED);
    }
      @GetMapping("/get")
      public StudentDto get(HttpServletResponse res,HttpServletRequest req,@RequestParam(name = "id") Integer id){
          Optional<Student> byId = studentRepository.findById(id);
          if (byId.isPresent()) {
              Student student = byId.get();
              GroupController.addCookie(req,res,"student_id",student.getId());
              return StudentDto.builder()
                      .id(student.getId())
                      .group_name(student.getGroups().getName())
                      .age(student.getAge())
                      .birthdate(student.getBirthDate())
                      .name(student.getName())
                      .gender(student.getGender())
                      .build();
          }
          throw new NotFoundException();
      }
    @GetMapping("/get-with-name")
    public StudentDto get(@RequestParam(name = "name") String name,HttpServletRequest req,HttpServletResponse res){
        Student student = studentRepository.findByName(name);
        if (student!=null) {
                GroupController.addCookie(req,res,"student_id",student.getId());
            System.out.println(student);
            return StudentDto.builder()
                    .id(student.getId())
                    .group_name(student.getGroups().getName())
                    .age(student.getAge())
                    .birthdate(student.getBirthDate())
                    .name(student.getName())
                    .gender(student.getGender())
                    .build();
        }
        throw new NotFoundException();
    }
    @GetMapping("/get.birthdate")
    public List<StudentDto> getStudentByBirthDates(@RequestParam Map<String,String> param){
        String s1 = param.get("one");
        String s2 = param.get("two");
        LocalDate date1 = LocalDate.parse(s1);
        LocalDate date2 = LocalDate.parse(s2);
        List<Student> students= studentRepository.findAllByBirthDates(date1,date2);
        List<StudentDto> studentDtos = new LinkedList<>();
        students.forEach(student -> {
                     studentDtos.add(StudentDto.builder()
                             .id(student.getId())
                             .age(student.getAge())
                             .gender(student.getGender())
                             .name(student.getName())
                             .birthdate(student.getBirthDate())
                             .group_name(student.getGroups().getName())
                             .build());
        });
        return studentDtos;
    }

    @PutMapping("/edit")
    public StudentDto edit(@RequestBody StudentDto studentDto,
                        HttpServletRequest req,HttpServletResponse res){
        Optional<Student.Gender> optionalGender = Arrays.stream(Student.Gender.values())
                .filter(gender -> gender.name().equals(studentDto.gender))
                .findFirst();

        Student.Gender gender = optionalGender.orElse(null);

        if (Objects.isNull(gender)) {
            throw new BadParamException();
        }

        String groupName = studentDto.group_name;
        Groups groups = groupRepository.findByName(groupName);
        if (Objects.isNull(groups)) {
            throw new NotFoundException();
        }
        System.out.println(groups);

        Student student = Student.builder()
                .id(studentDto.id)
                .name(studentDto.name)
                .age(studentDto.age)
                .groups(groups)
                .birthDate((studentDto.birthdate))
                .gender(gender.name()).build();
        Student save = studentRepository.save(student);
        groups.addStudent(save);
        Groups save1 = groupRepository.save(groups);

            addCookie(req,res,"student_id",save.getId());
            addCookie(req,res,"group_id",save1.getId());
        StudentLogger.updateEntity(save);

        return studentDto;
    }
    @DeleteMapping("/delete")
    public void delete(@RequestParam Integer id,HttpServletResponse res,HttpServletRequest req){
        Optional<Student> byId = studentRepository.findById(id);
        studentRepository.deleteWithId(byId.orElseThrow(BadParamException::new).getId());
        Student student = byId.get();
        deleteCookie(req,res,"student_id");
        StudentLogger.deleteEntity(student);
    }
    @DeleteMapping("/delete-with-name")
    public void delete(@RequestParam String name,HttpServletRequest req,HttpServletResponse res){
        Student byName = studentRepository.findByName(name);
        studentRepository.deleteStudentByName(name);
        if (byName!=null) {
            studentRepository.delete(byName);
            deleteCookie(req,res,"student_id");
            StudentLogger.deleteEntity(byName);
        }

    }
    @GetMapping("/get-with-group-id")
    public List<Student> getStudentsWithGroupId(@RequestParam(name = "group-id") Integer groupId){
        return studentRepository.findAllByGroupId(groupId);
    }
    protected static void addCookie(HttpServletRequest req,HttpServletResponse res,String name,Object value){
        Cookie[] array = req.getCookies();
        Cookie cookie;
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
