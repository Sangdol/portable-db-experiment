Portable DB Experiment
======================

An experimental project, handling millions of data with [Dropwizard](http://www.dropwizard.io/) and [H2](http://www.h2database.com/).

Goal
----

To handle millions of views per hour with a portable database.

Requirements
------------

* There are web pages for each users.
* There are 5 millions of page views per hour.
* There are pages that shows ID and the accessed dates of the recent 10 visitors of each user.

Requirements analysis
---------------------

### Record count

When there are 5 millions of data per hour

* 3.6 billions / month
* 120 millions / day
* 83 thousands / min
* 1,400 / sec

### Data usage

If a row takes up 20 bytes,

* 72G / month
* 2.4G / day
* 100M / hour
* 1.6M / min
* 26KB / sec

Let's check the reason why each row takes up 20 bytes.

### Schema

```sql
CREATE TABLE view (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  host_id INT,
  visitor_id INT,
  date TIMESTAMP
);
CREATE INDEX ON view (host_id);
```

* Used BIGINT(8 bytes) for ID because the maximum value of INT is only about 2 billions.
  * We have 3.6 billion views per month.
* Used INT(4 bytes) for user IDs as it's hardly possible to surpass 2 billion users.
* Used TIMESTAMP(4 bytes) for date instead of DATETIME(8 bytes) to save space.
  * We can change to DATETIME before [2038](http://en.wikipedia.org/wiki/Year_2038_problem).
* Given the schema, the size of each row would be 20 bytes (8+4+4+4).
  * Refer to [Data Type Storage Requirements](https://dev.mysql.com/doc/refman/5.5/en/storage-requirements.html).
* The size of the indices aren't counted in the calculation.

Data handling
-------------

### Need to delete any data?

Yes, because

* they take up too much storage.
* the performance of queries is getting inefficient as the number of rows grows.

### What to delete?

As we need only 10 recent view records for each user, we delete other than those.

### How to delete?

At this point, we need approximate the user count. Assuming a user visits ten times a day,

* we have about 10 million users
* 100 million rows(2G) when each user has 10 view records

Given the rows, it's not possible to manage the data well even with periodic deletion. So I chose to split the table horizontally.

### How to split?

Because deletion is very inefficient with limited functions of H2, I chose 1,000 for user count per table to make the deletion fast. Then we need 10,000 tables for 10 million users. The numbers can be adjusted depends of the environment or use cases.

* The deletion need to be very fast because a table is locked while deleting.
* Technically, there's [no limit for maximum number of tables](http://www.h2database.com/html/advanced.html?highlight=limit&search=Limit#limits_limitations).

### When to delete?

* In practice, we don't need to delete often if we assume that a user visits ten times a day.
* In the experiment, I'm going to create a lot of views for restricted users and delete redundant data every 10 seconds.

### What are trade-offs?

Cons of splitting tables:

* It's not easy to manage tables. e.g. changing schema, getting count of all rows, etc.
* Cannot create foreign keys.
  * Foreign keys are luxury when we need to handle such big data.
* It would be slow and complex to search with fields other than `host_id`, like `visitor_id` or `date`.

### I think I've heard of this concept

Right, this is similar to [Partitioning](https://dev.mysql.com/doc/refman/5.5/en/partitioning-overview.html). But H2 doesn't support partitioning, it's in [the roadmap](http://www.h2database.com/html/roadmap.html) though, and [no other local file based databases support partitioning](http://en.wikipedia.org/wiki/Comparison_of_relational_database_management_systems#Partitioning).

How to run
----------

### Web application server

1. Run the web application server by executing the below command in the project home directory.
  * `java -jar target/portable-db-experiment-1.0-SNAPSHOT.jar server database.yml`
2. When the server starts, you can access to users page which has view records.
  * [http://localhost:8080/user/1](http://localhost:8080/user/1)
3. You can see who visited where through the URLs in the server logs.
4. You can create views using `curl`.
  * e.g. `curl -X POST "http://localhost:8080/user/1?visitor_id=2"`

### Batch

Two batch scripts in `script/` directory.

1. `clear.sh` calls `delete` and `select` APIs. In this script:
  * it deletes redundant data every 10 seconds.
  * it shows current number of records of each tables.
2. `stress.sh` calls `create` API. In this script:
  * it keeps visiting random users pages.
  * it shows the number of records of each tables.
  * although we postulated that we get 1,400 requests per second, it's not easy to make that number of requests in a single machine.

### Experiment settings

For a faster and visible test, I've set values like below

* Table count: 100
* User(Host) count per table: 1,000
* User ID range: 1~10,000

With this settings,

* IDs between 1 and 100,000 (100 * 1,000) are acceptable to the server.
* Tables from 1st to 10th (10,000 / 1,000) are going to be filled.
* Record count of each table converges to 10,000 (1,000 * 10).
