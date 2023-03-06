package com.kt.myrestapi.lectures;

import com.kt.myrestapi.lectures.dto.LectureReqDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

@Controller
@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {
    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;


    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors) {
        //입력항목 체크
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }
        //입력항목의 biz logic 체크
        lectureValidator.validate(lectureReqDto, errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);
        Lecture savedLecture = lectureRepository.save(lecture);
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(LectureController.class)
                .slash(lecture.getId());//http://localhost:8080/api/lectures/10
        URI uri = linkBuilder.toUri();
        return ResponseEntity.created(uri).body(savedLecture);
    }
}
