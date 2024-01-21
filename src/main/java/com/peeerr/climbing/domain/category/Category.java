package com.peeerr.climbing.domain.category;

import com.peeerr.climbing.domain.BaseEntity;
import com.peeerr.climbing.domain.post.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Category extends BaseEntity {

    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(length = 20, nullable = false)
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Post> posts;

    public void changeCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static Category of(String category) {
        return new Category(category);
    }

    public static Category of(Long id, String category) {
        return new Category(id, category);
    }

    private Category(String categoryName) {
        this.categoryName = categoryName;
    }

    private Category(Long id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

}
