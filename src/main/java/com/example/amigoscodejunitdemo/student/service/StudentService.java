package com.example.amigoscodejunitdemo.student.service;

import com.example.amigoscodejunitdemo.student.exception.BadRequestException;
import com.example.amigoscodejunitdemo.student.exception.StudentNotFoundException;
import com.example.amigoscodejunitdemo.student.models.Student;
import com.example.amigoscodejunitdemo.student.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        Boolean existsEmail = studentRepository.selectExistsEmail(student.getEmail());
        if (existsEmail) {
            throw new BadRequestException("Email " + student.getEmail() + " taken");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Student with id " + studentId + " does not exists");
        }
        studentRepository.deleteById(studentId);
    }

    public Student findOne(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student dose not exist.")
        );
    }
}