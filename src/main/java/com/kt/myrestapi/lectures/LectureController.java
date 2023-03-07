package com.kt.myrestapi.lectures;

import com.kt.myrestapi.common.ErrorsResource;
import com.kt.myrestapi.lectures.dto.LectureReqDto;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {
    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    @PutMapping("/{id}")
    public ResponseEntity updateLecture(@PathVariable Integer id,
                                        @RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        //id와 매핑되는 Entity가 없으면 404 에러
        if(optionalLecture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " Lecture Not Found!");
        }
        //입력항목 체크해서 오류가 있다면 400 에러
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        //입력항목 Biz로직 체크해서 오류가 있다면 400 에러
        this.lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Lecture existingLecture = optionalLecture.get();
        this.modelMapper.map(lectureReqDto, existingLecture);
        Lecture savedLecture = this.lectureRepository.save(existingLecture);
        LectureResDto lectureResDto = modelMapper.map(savedLecture,
                LectureResDto.class);
        LectureResource LectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(LectureResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLecture(@PathVariable Integer id) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        if(optionalLecture.isEmpty()) {
            //return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " Lecture Not Found!");
        }
        Lecture lecture = optionalLecture.get();
        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping
    public ResponseEntity queryLectures(Pageable pageable,
                                        PagedResourcesAssembler<LectureResDto> assembler) {
        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);
        //Page<Lecture> => Page<LectureResDto>
        Page<LectureResDto> lectureResDtoPage =
                lecturePage.map(lecture -> modelMapper.map(lecture, LectureResDto.class));
        //1단계 - first,prev,next,last 링크
        //PagedModel<EntityModel<T>> toModel(Page<T> entity)
        //Page<LectureResDto> => PagedModel<EntityModel<LectureResDto>>
//        PagedModel<EntityModel<LectureResDto>> pagedResources =
//                assembler.toModel(lectureResDtoPage);

        //2단계 - first,prev,next,last 링크 + SelfLink 도 포함
        //public <R extends RepresentationModel<?>>
        //PagedModel<R> toModel(Page<T> page,RepresentationModelAssembler<T,R> assembler)
        //RepresentationModelAssembler의 추상메서드 R toModel(T entity)
        PagedModel<LectureResource> pagedResources =
                assembler.toModel(lectureResDtoPage, lectureResDto -> {
            return new LectureResource(lectureResDto);
        });
        return ResponseEntity.ok(pagedResources);
    }

    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors) {
        //입력항목 체크
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        //입력항목의 biz logic 체크
        lectureValidator.validate(lectureReqDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);

        //free, offline 값을 갱신
        lecture.update();

        Lecture savedLecture = lectureRepository.save(lecture);
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        WebMvcLinkBuilder linkBuilder = linkTo(LectureController.class)
                .slash(lecture.getId());//http://localhost:8080/api/lectures/10
        URI uri = linkBuilder.toUri();

        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
        lectureResource.add(linkBuilder.withRel("update-lecture"));

        return ResponseEntity.created(uri).body(lectureResource);
    }

    private static ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
