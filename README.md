# The Journalist - A Blogging Application - Spring Boot Microservices 

## Introduction
This project is a simple blogging platform developed using the Spring Boot microservices architecture. It includes a Spring Gateway and two core microservices: the User Service and the CMS (Content Management System) Service. This structure is designed to scale easily and ensure that components are loosely coupled and independently deployable.

## Architecture Overview
- **Spring Gateway**: Serves as the entry point for all incoming requests, effectively routing them to the appropriate backend service based on the URL path. This gateway also handles concerns such as SSL termination, API composition, and load balancing.
- **User Service**: Manages all user-related operations including user registration, user authentication, and profile management.
- **CMS Service**: Responsible for blog content operations such as creating, updating, and deleting blog posts.

![alt text](https://github.com/reuben21/the-journalist-blog-spring-boot/blob/dev/the-journalist-spring-boot.drawio.png)
