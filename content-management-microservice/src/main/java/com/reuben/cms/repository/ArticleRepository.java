package com.reuben.cms.repository;


import com.reuben.cms.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableTag;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.time.LocalDateTime;
import java.util.*;


@Repository
@AllArgsConstructor
public class ArticleRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private static final TableSchema<Article> schema = TableSchema.builder(Article.class)
            .newItemSupplier(Article::new)
            .addAttribute(String.class, attr -> attr.name("id")
                    .getter(Article::getId)
                    .setter(Article::setId)
                    .tags(StaticAttributeTags.primaryPartitionKey()))
            .addAttribute(String.class, attr -> attr.name("title")
                    .getter(Article::getTitle)
                    .setter(Article::setTitle))
            .addAttribute(String.class, attr -> attr.name("content")
                    .getter(Article::getContent)
                    .setter(Article::setContent))
            .addAttribute(EnhancedType.listOf(String.class),
                    attr -> attr.name("categories")
                            .getter(Article::getCategories)
                            .setter(Article::setCategories))
            .addAttribute(String.class, attr -> attr.name("status")
                    .getter(Article::getStatus)
                    .setter(Article::setStatus))
            .addAttribute(String.class, attr -> attr.name("publishDate")
                    .getter(Article::getPublishDate)
                    .setter(Article::setPublishDate))

            .build();

    private DynamoDbTable<Article> getTable() {
        return dynamoDbEnhancedClient.table("Article", schema);
    }

    public void save(Article article) {
        try {
            article.setId(UUID.randomUUID().toString());
            article.setPublishDate(LocalDateTime.now().toString());
            getTable().putItem(PutItemEnhancedRequest.builder(Article.class).item(article).build());

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    public Article findById(String articleId) {
        try {

            // Create a Key object to find the article with the given articleId
            Key key = Key.builder()
                    .partitionValue(articleId)
                    .build();

            Article article = getTable().getItem(key);
            article.setId(articleId);
            System.out.println("The article title is " + article.getTitle() + ". The publish date is " + article.getPublishDate());
            return article;

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);

        }

        return null;
    }

    public void queryTableWithPagination(String partitionValue,String startDate, String endDate, int pageSize) {
        try {

//            AttributeValue att = AttributeValue.builder().s(filterValue).build();

//            Map<String, AttributeValue> expressionValues = new HashMap<>();
//            expressionValues.put(":value", att);
//            Expression expression = Expression.builder()
//                    .expression("custName = :value")
//                    .expressionValues(expressionValues)
//                    .build();

            // Assuming 'registrationDate' is a sort key or part of a GSI's key schema
            Map<String, AttributeValue> expressionValues = new HashMap<>();
            expressionValues.put(":startDate", AttributeValue.builder().s(startDate).build());
            expressionValues.put(":endDate", AttributeValue.builder().s(endDate).build());

            // Adjust the expression based on your key schema
            Expression expression = Expression.builder()
                    .expression("registrationDate BETWEEN :startDate AND :endDate")
                    .expressionValues(expressionValues)
                    .build();

            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder().partitionValue(partitionValue).build());

            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(queryConditional)
//                    .filterExpression(expression)
                    .limit(pageSize)
                    .build();

            // Paginated query
            Iterator<Page<Article>> results = getTable().query(queryRequest).iterator();
            while (results.hasNext()) {
                Page<Article> page = results.next();
                page.items().forEach(System.out::println);

                // This example only fetches the first page. In a real application, you might loop through more pages.
                // If you want to fetch the next page in another request (e.g., for a web application), you'd pass the LastEvaluatedKey to the client.
                System.out.println("Fetched a page with " + page.items().size() + " items.");
                break; // Remove this break to fetch all pages in a loop.
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done");
    }

}
