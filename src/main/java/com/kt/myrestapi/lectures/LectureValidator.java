package com.kt.myrestapi.lectures;

import java.time.LocalDateTime;

import com.kt.myrestapi.lectures.dto.LectureReqDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class LectureValidator {
    public void validate(LectureReqDto lectureReqDto, Errors errors) {
        //basePrice > maxPrice 크면 오류 발생시킴
        if(lectureReqDto.getBasePrice() > lectureReqDto.getMaxPrice() &&
                lectureReqDto.getMaxPrice() != 0) {
            //Field Error
            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong");
            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong");
            //Global Error
            errors.reject("wrongPrices", "Values for prices are wrong");
        }
        //강의 등록 시작/종료일자가 강의종료 날짜보다 이후이면 에러를 발생시킴
        LocalDateTime endLectureDateTime = lectureReqDto.getEndLectureDateTime();
        if(endLectureDateTime.isBefore(lectureReqDto.getBeginLectureDateTime()) ||
                endLectureDateTime.isBefore(lectureReqDto.getCloseEnrollmentDateTime()) ||
                endLectureDateTime.isBefore(lectureReqDto.getBeginEnrollmentDateTime()) ) {
            errors.rejectValue("endLectureDateTime", "wrongValue", "endLectureDateTime is wrong");
        }
    }
}
