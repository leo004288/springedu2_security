package com.example.springedu2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Visitor {

    @Id  // 기본키 : primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 번호자동증가
    private Integer id; // 방명록 번호 id : 기본키

    // 작성자 : name
    @NotBlank(message = "이름은 필수입력입니다.")  // null, 빈문자열, " " 공백포함된 문자열 전부 허용 x
    @Size(max = 50)  // 문자열(50문자), 배열(50개), Arraylist(50개)
    private String name;

    // 작성일 : writeDate
    @CreationTimestamp  // data등록일 자동입력 (일일이 LocalDateTime.now를 넣지 않아도 됨)
    @Column (name = "writedate", nullable = false, updatable = false)
    private LocalDate writeDate;

    // 방명록 내용 : memo
    @NotBlank(message = "내용은 필수입력입니다.")
    @Size(max = 1000)
    private String memo;

}
