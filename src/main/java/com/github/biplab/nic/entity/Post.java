package com.github.biplab.nic.entity;

import com.github.biplab.nic.enums.Department;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "post_name", nullable = false)
    private String postName;

    @Column(name = "rank", nullable = false)
    private Integer rank;

    @Column(name = "department")
    private String department;

}