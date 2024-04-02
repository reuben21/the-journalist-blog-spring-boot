package com.reuben.cms.controller;


import com.reuben.cms.model.Article;
import com.reuben.cms.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleRepository articleRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createArticle(@RequestBody Article article) {
        System.out.println(article.getTitle());
        try {
            articleRepository.save(article);

            return ResponseEntity.ok("Article created: " + article.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating article: "+ e.getMessage());
        }


    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getArticle(@PathVariable String id) {
        try {
            Article article1 = articleRepository.findById(id);
            return ResponseEntity.ok(article1);
        } catch (DynamoDbException e) {
            return ResponseEntity.badRequest().body("Error getting article: "+ e.getMessage());
        }

    }




    @GetMapping("/list")
    public ResponseEntity<?> getArticles(
            @RequestParam(required = false, defaultValue = "3") Integer limit) {

        try {
//            List<Article> articles =
//            articleRepository.queryTableWithPagination("45b05cb6-133d-4bae-b08a-8afd90bab19f",limit);
            return ResponseEntity.ok("OK");
        } catch (DynamoDbException e) {
            return ResponseEntity.badRequest().body("Error getting articles: "+ e.getMessage());
        }
    }



}
