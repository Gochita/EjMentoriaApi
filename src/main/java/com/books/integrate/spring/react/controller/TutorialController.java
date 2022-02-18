package com.books.integrate.spring.react.controller;

import java.util.*;

import com.books.integrate.spring.react.model.Tutorial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.books.integrate.spring.react.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;


    @GetMapping("/tutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
        try {
            List<Tutorial> tutorials = new ArrayList<Tutorial>();

            if (title == null)
                tutorialRepository.findAll().forEach(tutorials::add);
            else
                tutorialRepository.findByTitleContaining(title).forEach(tutorials::add);

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    //Metodo para consultar cursos por precio
    @GetMapping("/price/{price}")
    public List<Tutorial> getTitleByPrice(@PathVariable("price") double price) {
        List<Tutorial> tutorialData = tutorialRepository.findTitleByPrice(price);

        return tutorialData;


    }
    @GetMapping("/tutorials/published")
    public ResponseEntity<List<Tutorial>> findByPublished() {
        try {
            List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        try {
            Tutorial _tutorial = tutorialRepository
                    .save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false, tutorial.getPrice()));
            return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            Tutorial _tutorial = tutorialData.get();
            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            _tutorial.setPrice(tutorial.getPrice());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
//Metodo para actualizar un curso por su titulo
    @PutMapping("/tutorials/title/{title}")
    public ResponseEntity<Tutorial> updateTutorialByTitle(@PathVariable("title")String title,@RequestBody Tutorial tutorialdata){
        try{
            List<Tutorial> tutoriales= tutorialRepository.findByTitleContaining(title.replace("-", " "));
            Tutorial tutorialData= tutoriales.iterator().next();
            tutorialData.setTitle(tutorialdata.getTitle());
            tutorialData.setDescription(tutorialdata.getDescription());
            tutorialData.setPublished(true);
            tutorialData.setPrice(tutorialdata.getPrice());
            return new ResponseEntity<>(tutorialRepository.save(tutorialData),HttpStatus.OK);

        }catch(Exception ex){
            return new ResponseEntity<>((HttpStatus.NOT_FOUND));
        }
    }


    //HttpStatus
    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<String> deleteTutorial(@PathVariable("id") long id) {
        try {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>("Tutorials DELETE!! ", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            tutorialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }

    //Metodo para eliminar curso por titulo
    @DeleteMapping("/tutorialsDe")
    public String deleteByName(@RequestParam("title") String title) {

        //Para borrar un curso que tiene espacios en blanco en el nombre
        //se debe colocar un guion '-' en vez de espacio para que el metodo funcione
        try {
            List<Tutorial> tutorialData = tutorialRepository.findByTitleContaining(title.replace("-", " "));
            if (!tutorialData.isEmpty()) {
                tutorialRepository.deleteById(tutorialData.get(0).getId());
            }
            return "Se elimino el curso " + title;
        } catch (Exception e) {
            return "No se pudo eliminar ";
        }


    }



}
