package com.example.amigoscodejunitdemo.student;

import com.example.amigoscodejunitdemo.student.enums.Gender;
import com.example.amigoscodejunitdemo.student.exception.BadRequestException;
import com.example.amigoscodejunitdemo.student.exception.StudentNotFoundException;
import com.example.amigoscodejunitdemo.student.models.Student;
import com.example.amigoscodejunitdemo.student.repository.StudentRepository;
import com.example.amigoscodejunitdemo.student.service.StudentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTestTrail {

    @Mock
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        this.studentService = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        studentService.getAllStudents();
        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        //given
        Student student = Student
                .builder()
                .id(1L)
                .name("Ahmed")
                .email("ahmed@gmail.com")
                .gender(Gender.MALE)
                .build();

        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);
//        given(studentRepository.save(studentArgumentCaptor.capture())).willReturn(student);
        //when
        studentService.addStudent(student);
        //then
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student actualStudent = studentArgumentCaptor.getValue();
        Assertions.assertThat(student).isEqualTo(actualStudent);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = Student
                .builder()
                .id(1L)
                .name("Ahmed")
                .email("ahmed@gmail.com")
                .gender(Gender.MALE)
                .build();
        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);
        // when
        Assertions.assertThatThrownBy(() -> studentService.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");
        // then
        verify(studentRepository, never()).save(student);
    }

    @Test
    void canDeleteStudent() {
        // given
        Long studentId = 1L;
        given(studentRepository.existsById(studentId)).willReturn(true);
        // when
        studentService.deleteStudent(studentId);
        // then
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void willThrowWhenStudentNotExist() {
        // given
        Long studentId = 1L;
        given(studentRepository.existsById(studentId)).willReturn(false);
        // when
        Assertions.assertThatThrownBy(() -> studentService.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");
        // then
        verify(studentRepository, never()).deleteById(studentId);
    }

    @Test
    void canFindOne() {
        // given
        Long studentId = 1L;
        Student student = Student
                .builder()
                .id(1L)
                .name("Ahmed")
                .email("ahmed@gmail.com")
                .gender(Gender.MALE)
                .build();
        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        // when
        studentService.findOne(studentId);
        // then
        verify(studentRepository).findById(studentId);
    }

    @Test
    void willThrowFindOne() {
        // given
        Long studentId = 1L;
        given(studentRepository.findById(studentId)).willReturn(Optional.empty());
        // when
        Assertions.assertThatThrownBy(() -> studentService.findOne(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student dose not exist.");
        // then
        verify(studentRepository).findById(studentId);
    }
}
