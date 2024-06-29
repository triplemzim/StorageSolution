# StorageSolution

## Overview

StorageSolution is a Spring Boot application designed for easy sharing of files via the cloud. It utilizes MongoDB for storage, enabling users to upload, manage, and access their files efficiently.

## Features

- **File Upload**: Upload files with metadata including visibility and tags.
  - Maximum 5 tags allowed per file
  - There is no limit on file size
  - Visibility can be -
    - `PUBLIC`
    - `PRIVATE`
- **File Download**: Download any files with given link
  - Anyone can download the file with the link
- **File Management**: Rename and delete files based on user access.
- **Public and User Files**: Access public files or files specific to a user with pagination and sorting.
- **Tag Filtering**: Filter files by tags.

## Technologies

- **Spring Boot**
- **MongoDB**
- **Apache Tika**
- **JUnit & Mockito for testing**

## Prerequisites

- **Docker**
- **Java 17**
- **Maven**

## Setup Instructions

1. **Clone the repository**:

    ```sh
    git clone https://github.com/triplemzim/StorageSolution.git
    cd StorageSolution
    ```

2. **Start MongoDB using Docker Compose**:

   Make sure you have Docker and Docker Compose installed. Use the provided `docker-compose.yml` file to set up MongoDB:

    ```sh
    docker-compose -f docker_mongodb.yml -p storage up -d
    ```

3. **Run the Spring Boot application**:

   Ensure you have Java 11 or higher and Maven installed. Run the application using Maven:

    ```sh
    mvn spring-boot:run
    ```

## API Endpoints

### File Upload

- **URL**: `/api/storage/v1/files/upload`
- **Method**: `POST`
- **Parameters**:
    - `file` (MultipartFile)
    - `visibility` (String)
      - `PUBLIC`
      - `PRIVATE`
    - `tags` (String)

### File Download

- **URL**: `/api/storage/v1/files/{fileId}/download`
- **Method**: `GET`
- **Parameters**:
    - `fileId` (String)

### Delete File

- **URL**: `/api/storage/v1/files/delete`
- **Method**: `DELETE`
- **Parameters**:
    - `user` (String)
    - `filename` (String)

### Rename File

- **URL**: `/api/storage/v1/files/rename`
- **Method**: `PUT`
- **Parameters**:
    - `user` (String)
    - `filename` (String)
    - `newFilename` (String)

### Get Public Files

- **URL**: `/api/storage/v1/files/public`
- **Method**: `GET`
- **Parameters**:
    - `page` (int, default: 0)
    - `sortBy` (String, default: "filename", optional)
      - Options
        - filename
        - fileSize
        - uploadDate
        - contentType
        - tag
    - `filterByTag` (String, optional)
    - `page` (String, optional)
- Sample Response
  - ```
    {
        "content": [
            {
                "fileName": "renamed.pdf",
                "visibility": "PUBLIC",
                "tags": [
                    "myTag0",
                    "myTag01"
                ],
                "uploadDate": "2024-06-29T03:13:05.781+00:00",
                "downloadLink": "http://localhost:8080/api/storage/v1/files/667f7bc1ca763c166bea7c64/download"
            }
        ],
        "page": {
            "size": 5,
            "number": 0,
            "totalElements": 1,
            "totalPages": 1
        }
    }
    ```

### Get User Files

- **URL**: `/api/storage/v1/files/user`
- **Method**: `GET`
- **Parameters**:
    - `user` (String)
    - `page` (int, default: 0)
    - `sortBy` (String, default: "filename", optional)
      - Options
        - filename
        - fileSize
        - uploadDate
        - contentType
        - tag
    - `filterByTag` (String, optional)
    - `page` (String, optional)
- Sample Response:
  - ```
    {
      "content": [
          {
              "fileName": "renamed.pdf",
              "visibility": "PUBLIC",
              "tags": [
                  "myTag0",
                  "myTag01"
              ],
              "uploadDate": "2024-06-29T03:13:05.781+00:00",
              "downloadLink": "http://localhost:8080/api/storage/v1/files/667f7bc1ca763c166bea7c64/download"
          }
      ],
      "page": {
          "size": 5,
          "number": 0,
          "totalElements": 1,
          "totalPages": 1
      }
    }
    ```

## Testing

Unit tests are provided to ensure the functionality of the service. To run the tests:

```sh
mvn test
```

## Contact
For any questions or feedback, please contact triplemzim@gmail.com.