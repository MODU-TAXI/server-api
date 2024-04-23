package com.modutaxi.api.domain.onboarding.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Onboarding extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int questionId;

    @Builder.Default
    @ColumnDefault("0")
    private int etcCount = 0;
    @Builder.Default
    @ColumnDefault("0")
    private int answer1Count = 0;
    @Builder.Default
    @ColumnDefault("0")
    private int answer2Count = 0;
    @Builder.Default
    @ColumnDefault("0")
    private int answer3Count = 0;

    @Builder.Default
    @ColumnDefault("0")
    private int respondentCount = 0;    // 응답자의 총 수

    public void upAnswerCount(boolean etc, boolean answer1, boolean answer2, boolean answer3) {
        if(etc) this.etcCount++;
        if(answer1) this.answer1Count++;
        if(answer2) this.answer2Count++;
        if(answer3) this.answer3Count++;
    }

    public void upRespondentCount() {
        this.respondentCount++;
    }

}