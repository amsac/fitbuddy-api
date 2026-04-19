package com.fitbuddy.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String externalId; // from Excel (exerciseId)

    private String imageUrl;

    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    private String muscleGroup;
}

//="INSERT INTO public.exercise (name, external_id, image_url, video_url, overview, instructions, muscleGroup) VALUES ('"
//        & SUBSTITUTE(B2,"'","''") & "','"
//        & SUBSTITUTE(A2,"'","''") & "','"
//        & SUBSTITUTE(C2,"'","''") & "','"
//        & SUBSTITUTE(D2,"'","''") & "','"
//        & SUBSTITUTE(E2,"'","''") & "','"
//        & SUBSTITUTE(F2,"'","''") & "','chest');"

