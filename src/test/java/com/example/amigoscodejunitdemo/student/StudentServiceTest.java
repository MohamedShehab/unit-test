package com.example.amigoscodejunitdemo.student;

import com.example.amigoscodejunitdemo.student.enums.Gender;
import com.example.amigoscodejunitdemo.student.exception.BadRequestException;
import com.example.amigoscodejunitdemo.student.exception.StudentNotFoundException;
import com.example.amigoscodejunitdemo.student.models.Student;
import com.example.amigoscodejunitdemo.student.repository.StudentRepository;
import com.example.amigoscodejunitdemo.student.service.StudentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        List<Student> students = new ArrayList<>();
        Student student1 = new Student(1L, "Mohamed", "moahmed@gmail.com", Gender.MALE);
        students.add(student1);
        when(studentRepository.findAll()).thenReturn(students);
        List<Student> list = underTest.getAllStudents();
        // then
        Assertions.assertEquals(students.size(), list.size());
        for (Student studentExpected : students) {
            for (Student actualStudent : list) {
                Assertions.assertEquals(studentExpected.getName(), actualStudent.getName());
                Assertions.assertEquals(studentExpected.getEmail(), actualStudent.getEmail());
            }
        }
//        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);
        Student student = new Student(1L, "Ahmed", "ahmed123@gmail.com", Gender.MALE);
        underTest.addStudent(student);
        verify(studentRepository).save(studentArgumentCaptor.capture());
        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
        Assertions.assertEquals(student.getName(), capturedStudent.getName());
        Assertions.assertEquals(student.getEmail(), capturedStudent.getEmail());
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student(
                "Jamila",
                "jamila@gmail.com",
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository,never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}
