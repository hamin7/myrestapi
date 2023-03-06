package com.kt.myrestapi.lectures;

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
public class LectureController {

    @PostMapping
    public ResponseEntity createLecture(@RequestBody Lecture lecture) {
        lecture.setId(10);
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(LectureController.class)
                .slash(lecture.getId());//http://localhost:8080/api/lectures/10
        URI uri = linkBuilder.toUri();

    }
}
