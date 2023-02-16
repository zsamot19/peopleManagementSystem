package com.tomasz.peopledbweb.biz.service;

import com.tomasz.peopledbweb.biz.data.FileStorageRepository;
import com.tomasz.peopledbweb.biz.data.PersonRepository;
import com.tomasz.peopledbweb.biz.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class PersonService {
    private final PersonRepository personRepository;
    private final FileStorageRepository fileStorageRepository;

    public PersonService(PersonRepository personRepository, FileStorageRepository fileStorageRepository) {
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    @Transactional
    public Person save(Person person, InputStream inputStream) {
        Person savedPerson = personRepository.save(person);
        if(savedPerson.getPhotoFileName()==""){
            person.setPhotoFileName("default.png");
            return savedPerson;
        }
        fileStorageRepository.save(person.getPhotoFileName(), inputStream);
        return savedPerson;
    }

    public Optional<Person> findById(Long aLong) {
        return personRepository.findById(aLong);
    }

    public Iterable<Person> findAll() {
        return personRepository.findAll();
    }

    public void deleteAllById(Iterable<Long> ids) {
        Iterable<Person> peopleToDelete = personRepository.findAllById(ids);
        Stream<Person> peopleStream = StreamSupport.stream(peopleToDelete.spliterator(), false);

        Set<String> fileNames = peopleStream
                .map(Person::getPhotoFileName)
                .collect(Collectors.toSet());

        personRepository.deleteAllById(ids);
        fileStorageRepository.deleteAllByName(fileNames);
    }

    public String exportToCsv(Optional<List<Long>> selections) {
        StringBuilder stringWriter = new StringBuilder();

        Iterable<Person> people = (selections.isEmpty() ?
                                            personRepository.findAll():
                                            personRepository.findAllById(selections.get()));

        stringWriter.append("Id,First Name, Last Name, Date Of Birth, Salary, Email\n");
        for (Person person : people) {
                    stringWriter
                            .append(person.getId()).append(",")
                            .append(person.getFirstName()).append(",")
                            .append(person.getLastName()).append(",")
                            .append(person.getDob()).append(",")
                            .append(person.getSalary()).append(",")
                            .append(person.getEmial()).append(",")
                            .append("\n");
        }
        return stringWriter.toString();
    }
}
