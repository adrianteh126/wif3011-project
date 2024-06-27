# WIF3011 Concurrent and Parallel Programming Group Project

## Project Description
Study the concept and algorithm of Bag-of-Words (BoW). Design and implement a sequential and two (2) versions of the BoW algorithm in Java. Then, develop an application to count and display frequencies of words in large text files using the sequential and concurrent BoW implementations. Compare the performances of the three implementations in accomplishing the task under different scenarios, e.g. on text files of different sizes; in computers with different hardware specifications, etc.

## Algorithm Design
- General flow to generate BoW
  - Convert file to string representation
  - String being filtered to remove stop words
  - Count the words using different concurrent strategy
- Sequential 
  - All process performed sequentially
- Concurrent 1 (Java Strean API)
  - Java Stream API is utilized to perform word count operation in parallel by leveraging internal common fork-join pool
- Concurrent 2 (Fork & Join)
  - Utilize Java fork and join API to process task parallelly

## Misc
- Frontend (port 3000)
  - Reactjs
- Backend (port 4000)
  - Springboot 