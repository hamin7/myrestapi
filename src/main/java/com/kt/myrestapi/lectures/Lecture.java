package com.kt.myrestapi.lectures;

import com.kt.myrestapi.LectureStatus;

import java.time.LocalDateTime;

public class Lecture {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginLectureDateTime;
    private LocalDateTime endLectureDateTime;
    private String location;
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private LectureStatus lectureStatus = LectureStatus.DRAFT;
}
