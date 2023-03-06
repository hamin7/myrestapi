package com.kt.myrestapi.lectures;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {
    private final LectureRepository lectureRepository;

    @PostMapping
    public ResponseEntity createLecture(@RequestBody Lecture lecture) {
        Lecture savedLecture = lectureRepository.save(lecture);
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(LectureController.class)
                .slash(lecture.getId());//http://localhost:8080/api/lectures/10
        URI uri = linkBuilder.toUri();
        return ResponseEntity.created(uri).body(savedLecture);
    }
}
