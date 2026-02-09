# Coffee-Shop

# Stage 1
Stage 1 is designed to assess your understanding of planned iterative development and your
knowledge of data structures, exception handling and unit testing, which are all taught in the first
half of the course. You are required to develop a simulation of a coffee shop. This first stage develops
the basic functionality, which you will then extend in Stage 2.
## Functional Requirements
For Stage 1, your application should load in details of a menu and a list of existing orders, show a
simple interface for processing new orders, and generate a summary report when exited.
---
1. A text file should be provided that shows the details of each item that a customer can order,
   including a brief description, the item’s cost, the item’s category, and a unique identifier.
   • There should be at least three categories: e.g. beverages, food items, other items
   • The item identifiers must follow the pattern <CATEGORY>-XXX, where the category prefix is
   alphabetic and the numeric suffix consists of three digits.

2. Another text file should be provided that contains a list of existing customer orders. Each order
   should include a timestamp, a unique identifier for the customer, and details of the item
   ordered. If a customer orders multiple items, these will appear as multiple orders with the same
   customer number.
3. These text files are read at the start of the application, and your code can assume that they are
   correctly formatted, e.g. the right number of commas in a CSV file. You should check that any
   identifiers are valid according to your rules.
4. Once the application is running, it can accept new orders from customers. These should be
   entered by the user using a simple GUI. The GUI should allow one or more items to be selected
   from a list of available items, and then display a bill. To make things more interesting (including
   your unit tests – see later) you should come up with some rules for discounts, e.g. get 20% off
   when you order a beverage and two food items.
5. Before the application exits, it should generate a report. As a minimum, this should list all the
   items in the menu, the number of times each item was ordered, and the total cost for all orders.
   Software Engineering Requirements
   These are the software engineering requirements for Stage 1:
1. Your application should be implemented using Java.
2. Develop your program using planned iterative development. In this stage you should do all the
   design before writing the code. Decide on all the classes for this stage, their instance variables
   and methods. Try using CRC cards to help with class design, and use other diagrams where
   appropriate. Make a plan to divide the work between you, in such a way each person can work
   independently where possible. How will you test your code? Decide when and how often you
   need to meet or be in contact. How will you integrate your work?
   F21AS Coursework
3. Base all your decisions just on the requirements for this stage. Do not use agile development at
   this stage, and do not plan ahead to Stage 2. However, do take notes about the development
   process and your experiences, since you will be asked to summarise this in your report.
4. Your program should read the data from the files at the start, and store it into appropriate data
   structures. When reading the files, don’t think ahead to the GUI or reports; just store the data
   so that it can be accessed easily (e.g. is a list suitable? would a map be useful?). Then, write
   methods to analyse the data, which is likely to involve using more data structures. When making
   your decisions, imagine that you have a large number of customers, orders and menu items.
5. Use version control. Your group should set up a repository, and a link to this repository should
   be included in your report. Note that we will check the commit history, so make sure this
   reflects your individual contributions.
6. Use exceptions to catch errors in the data. Each group should decide what makes valid data
   (e.g., length, range, number of characters, etc.) If an error is found, just continue without that
   line of data. Provide suitable data to check that your program is working correctly, e.g. input
   files with some errors.
7. You should throw exceptions in the constructor of at least one class, to ensure that the objects
   of that class that you create are valid (e.g. menu item identifiers), and you should write at least
   one of your own exception classes.
8. Use JUnit to test some of your constructors and/or methods, particularly ones involving
   calculations, e.g. the method that applies your discount rules. You could try test-driven
   development for these methods. If you create a JUnit test for a method, you should test all the
   paths in the method, not just one.