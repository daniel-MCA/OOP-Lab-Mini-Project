package com.qbank.config;

import com.qbank.model.Question;
import com.qbank.model.User;
import com.qbank.repository.QuestionRepository;
import com.qbank.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedQuestions();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "Platform Admin", User.Role.ADMIN));
        }
        if (!userRepository.existsByUsername("student")) {
            userRepository.save(new User("student", passwordEncoder.encode("student123"), "Demo Student", User.Role.STUDENT));
        }
    }

    private void seedQuestions() {
        if (questionRepository.count() > 0) return;

        add("JAVA", "OOP", "Which keyword prevents a class from being subclassed?",
                "static", "final", "abstract", "private", "B", "'final' on a class means it cannot be extended.");
        add("JAVA", "OOP", "What is the default value of an instance variable of type boolean?",
                "true", "false", "null", "0", "B", "Boolean instance fields default to false.");
        add("JAVA", "Collections", "Which interface does HashMap implement?",
                "List", "Set", "Map", "Queue", "C", "HashMap implements the Map interface.");
        add("JAVA", "Collections", "Which collection maintains insertion order and allows duplicates?",
                "HashSet", "ArrayList", "TreeSet", "HashMap", "B", "ArrayList preserves insertion order and allows duplicate elements.");
        add("JAVA", "Exceptions", "Which block always executes regardless of exception?",
                "catch", "try", "finally", "throw", "C", "The finally block always runs, exception or not.");
        add("JAVA", "Multithreading", "Which method causes a thread to pause and give other threads a chance to run?",
                "run()", "start()", "sleep()", "wait() only", "C", "Thread.sleep() pauses the current thread for a specified time.");
        add("JAVA", "Streams", "Which Stream method transforms each element without changing element count?",
                "filter", "map", "reduce", "collect", "B", "map() applies a function to each element 1-to-1.");
        add("JAVA", "Generics", "What does '? extends T' represent in generics?",
                "Lower bound", "Upper bound", "Exact type", "No bound", "B", "'extends' sets an upper bound wildcard.");

        add("PYTHON", "Basics", "Which of these is immutable in Python?",
                "list", "dict", "tuple", "set", "C", "Tuples cannot be modified after creation.");
        add("PYTHON", "Basics", "What does the 'len()' function return for a string?",
                "Memory size", "Number of characters", "ASCII sum", "Encoding type", "B", "len() returns the character count.");
        add("PYTHON", "Functions", "Which keyword is used to create an anonymous function?",
                "func", "def", "lambda", "anon", "C", "lambda defines small anonymous functions.");
        add("PYTHON", "OOP", "Which method is the constructor in a Python class?",
                "__new__", "__init__", "__main__", "__class__", "B", "__init__ initializes a newly created object.");
        add("PYTHON", "Data Structures", "Which data structure uses LIFO order natively via list methods append/pop?",
                "Queue", "Stack", "Dict", "Set", "B", "append()/pop() on a list gives LIFO stack behavior.");
        add("PYTHON", "Error Handling", "Which block runs no matter what in Python exception handling?",
                "except", "try", "finally", "raise", "C", "finally always executes.");

        add("CPP", "Basics", "Which operator is used for dynamic memory allocation in C++?",
                "malloc", "new", "alloc", "create", "B", "'new' allocates memory dynamically in C++.");
        add("CPP", "OOP", "Which concept allows a function to behave differently based on the object calling it?",
                "Encapsulation", "Polymorphism", "Abstraction", "Inheritance", "B", "Polymorphism enables behavior to vary by object type.");
        add("CPP", "Pointers", "What does the '->' operator do?",
                "Dereferences a pointer and accesses a member", "Adds two pointers", "Compares pointers", "Casts a pointer", "A",
                "-> dereferences a pointer to access a member of the pointed-to object.");
        add("CPP", "STL", "Which STL container provides O(1) average time complexity for key lookup?",
                "vector", "list", "unordered_map", "map", "C", "unordered_map uses hashing for average O(1) lookup.");
        add("CPP", "Memory", "What causes a memory leak in C++?",
                "Using new without matching delete", "Using stack variables", "Using references", "Using const", "A",
                "Failing to delete dynamically allocated memory leaks it.");

        add("OS", "Processes", "What is a process control block (PCB) used for?",
                "Storing process state information", "Compiling code", "Rendering graphics", "Managing DNS", "A",
                "A PCB stores all information the OS needs to manage a process.");
        add("OS", "Scheduling", "Which scheduling algorithm can cause starvation?",
                "Round Robin", "FCFS", "Priority Scheduling", "None of these", "C",
                "Low-priority processes may starve under strict priority scheduling.");
        add("OS", "Memory Management", "What is thrashing?",
                "Excessive paging causing low CPU utilization", "Fast context switching", "Efficient caching", "Deadlock avoidance",
                "A", "Thrashing happens when the system spends more time paging than executing.");
        add("OS", "Concurrency", "What is a deadlock?",
                "Two or more processes waiting indefinitely for each other's resources", "A crashed process",
                "A fast context switch", "Efficient memory sharing", "A",
                "Deadlock is a cycle of processes each waiting on a resource held by another.");
        add("OS", "File Systems", "What does an inode store?",
                "File metadata", "File name only", "The OS kernel", "Network address", "A",
                "An inode stores metadata about a file, not its name.");

        add("DSA", "Arrays", "What is the time complexity of accessing an element in an array by index?",
                "O(n)", "O(log n)", "O(1)", "O(n log n)", "C", "Array indexing is constant time.");
        add("DSA", "Trees", "What is the height of a balanced binary search tree with n nodes?",
                "O(n)", "O(log n)", "O(n^2)", "O(1)", "B", "A balanced BST has height proportional to log n.");
        add("DSA", "Sorting", "Which sorting algorithm has the best average-case time complexity?",
                "Bubble Sort", "Insertion Sort", "Merge Sort", "Selection Sort", "C", "Merge Sort runs in O(n log n) on average.");
        add("DSA", "Graphs", "Which algorithm finds the shortest path in a weighted graph with non-negative edges?",
                "DFS", "BFS", "Dijkstra's algorithm", "Topological sort", "C", "Dijkstra's algorithm finds shortest paths with non-negative weights.");
        add("DSA", "Dynamic Programming", "What technique does DP use to avoid recomputation?",
                "Recursion only", "Memoization/tabulation", "Random sampling", "Greedy choice", "B",
                "DP stores subproblem results (memoization/tabulation) to avoid recomputation.");
        add("DSA", "Stacks & Queues", "Which data structure is used to check balanced parentheses efficiently?",
                "Queue", "Stack", "Heap", "Graph", "B", "A stack naturally tracks nested opening/closing symbols.");
        add("JAVA", "Interfaces", "Which keyword is used when a class implements an interface?", "extends", "implements", "inherits", "instanceof", "B", "A class uses implements to provide an interface implementation.");
        add("JAVA", "Strings", "Which class is mutable and commonly used for efficient string concatenation?", "String", "StringBuilder", "Character", "Scanner", "B", "StringBuilder provides a mutable sequence of characters.");
        add("JAVA", "JVM", "Which component executes Java bytecode?", "JVM", "JDK", "Javadoc", "JAR", "A", "The Java Virtual Machine executes bytecode.");
        add("PYTHON", "Lists", "Which list method adds one item to the end of a list?", "push", "append", "insertEnd", "addLast", "B", "append adds an item at the end.");
        add("PYTHON", "Dictionaries", "Which method safely retrieves a dictionary value with a fallback?", "find", "get", "value", "fetchOnly", "B", "dict.get(key, default) supports a fallback.");
        add("PYTHON", "Loops", "Which keyword skips the rest of the current loop iteration?", "break", "continue", "pass", "skip", "B", "continue moves to the next iteration.");
        add("CPP", "References", "Which symbol declares a reference in C++?", "&", "*", "#", "@", "A", "An ampersand declares a reference.");
        add("CPP", "Classes", "Which access specifier makes members private by default in a class?", "public", "private", "protected", "internal", "B", "Class members are private by default.");
        add("CPP", "Virtual Functions", "Which keyword enables runtime polymorphism for a member function?", "static", "virtual", "friend", "inline", "B", "virtual enables dynamic dispatch.");
        add("OS", "Processes", "Which state means a process is waiting for CPU allocation?", "Running", "Ready", "Blocked", "Terminated", "B", "A ready process is waiting to be scheduled on the CPU.");
        add("OS", "Memory Management", "Which page replacement algorithm uses the page that has not been used for the longest time?", "FIFO", "LRU", "FCFS", "SJF", "B", "LRU replaces the least recently used page.");
        add("OS", "Scheduling", "What is a context switch?", "Changing the CPU from one process/thread to another", "Deleting a process", "Formatting memory", "Creating a file", "A", "The OS saves one execution context and restores another.");
        add("DSA", "Searching", "What is the worst-case time complexity of binary search on a sorted array?", "O(1)", "O(log n)", "O(n)", "O(n log n)", "B", "Binary search halves the search range each step.");
        add("DSA", "Linked Lists", "Which operation is O(1) when inserting at the head of a linked list?", "Insertion", "Search", "Sorting", "Index access", "A", "The head pointer can be updated directly.");
        add("DSA", "Heaps", "Which structure is commonly used to implement a priority queue?", "Heap", "Stack", "Array only", "Trie", "A", "A binary heap provides efficient priority queue operations.");


        add("JAVA", "OOP", "Which principle hides an object's internal implementation details?", "Inheritance", "Encapsulation", "Polymorphism", "Overloading", "B", "Encapsulation bundles state and behavior while controlling access to implementation details.");
        add("JAVA", "Collections", "Which collection does not allow duplicate elements?", "ArrayList", "LinkedList", "HashSet", "Vector", "C", "A Set does not permit duplicate elements.");
        add("JAVA", "Exceptions", "Which keyword explicitly raises an exception?", "throws", "throw", "catch", "finally", "B", "throw is used to explicitly throw an exception object.");
        add("JAVA", "Interfaces", "Can a Java interface contain default methods?", "No", "Yes", "Only constructors", "Only private fields", "B", "Java interfaces can declare default methods with implementations.");
        add("JAVA", "JVM", "Which memory area stores local variables and method call frames?", "Heap", "Stack", "Method area only", "String pool", "B", "Each thread has a stack containing frames for active method calls.");
        add("JAVA", "Generics", "What is the main benefit of Java generics?", "Faster CPU execution", "Compile-time type safety", "Automatic threading", "Garbage collection", "B", "Generics improve type safety and reduce unsafe casts at compile time.");
        add("JAVA", "Streams", "Which operation keeps elements matching a predicate?", "map", "filter", "reduce", "forEach", "B", "filter retains elements for which the predicate is true.");
        add("JAVA", "Multithreading", "Which keyword protects a critical section using an intrinsic monitor?", "volatile", "synchronized", "transient", "native", "B", "synchronized provides mutual exclusion using an object's monitor.");

        add("PYTHON", "Basics", "Which symbol starts a single-line comment in Python?", "//", "#", "--", "/*", "B", "Python uses # for single-line comments.");
        add("PYTHON", "Functions", "Which keyword returns a value from a Python function?", "yield", "return", "send", "give", "B", "return ends the function and optionally provides a value.");
        add("PYTHON", "Data Structures", "Which built-in type stores key-value pairs?", "tuple", "list", "dict", "set", "C", "A dictionary stores data as key-value pairs.");
        add("PYTHON", "OOP", "Which keyword refers to the current object inside a Python instance method?", "this", "self", "current", "object", "B", "self conventionally refers to the current instance.");
        add("PYTHON", "Error Handling", "Which keyword is used to deliberately raise an exception?", "raise", "throw", "error", "except", "A", "Python uses raise to trigger an exception.");
        add("PYTHON", "Comprehensions", "Which syntax creates a list using an expression and iteration?", "List comprehension", "Generator only", "Decorator", "Context manager", "A", "List comprehensions compactly construct lists from iterable data.");
        add("PYTHON", "Modules", "Which statement imports a module named math?", "include math", "using math", "import math", "require math", "C", "Python modules are imported with the import statement.");
        add("PYTHON", "Loops", "Which loop is commonly used to iterate directly over items in an iterable?", "foreach", "for", "repeat", "loop", "B", "Python's for loop iterates over items in an iterable.");

        add("CPP", "Basics", "Which header provides std::cout?", "<iostream>", "<stdio>", "<console>", "<output>", "A", "std::cout is declared by the iostream header.");
        add("CPP", "OOP", "Which feature allows one class to derive from another?", "Composition", "Inheritance", "Casting", "Templating", "B", "Inheritance creates a derived class from a base class.");
        add("CPP", "STL", "Which container stores elements in contiguous memory and supports dynamic resizing?", "vector", "set", "map", "stack", "A", "std::vector is a dynamically resizable contiguous sequence container.");
        add("CPP", "Pointers", "What does a null pointer represent?", "A pointer to the first element", "A pointer that points to no valid object", "A constant integer", "A reference", "B", "A null pointer does not point to a valid object or function.");
        add("CPP", "Templates", "What do function templates enable?", "Compile-time generic code", "Runtime garbage collection", "Automatic networking", "File compression", "A", "Templates allow generic code to be instantiated for different types.");
        add("CPP", "Memory", "Which operator releases memory allocated with new?", "free", "delete", "remove", "release", "B", "delete releases a single object allocated with new.");
        add("CPP", "References", "Can a reference normally be reseated to refer to another object?", "Yes", "No", "Only in arrays", "Only for pointers", "B", "A reference is bound to its referred object after initialization.");
        add("CPP", "STL", "Which container keeps key-value pairs ordered by key?", "unordered_map", "map", "vector", "queue", "B", "std::map maintains keys in sorted order.");

        add("OS", "Processes", "Which data structure is commonly used for the ready queue?", "Queue", "Stack only", "Tree only", "Hash only", "A", "Ready processes are typically maintained in a scheduling queue.");
        add("OS", "Scheduling", "Which algorithm assigns a fixed time quantum to processes?", "Round Robin", "FCFS", "SJF", "FIFO paging", "A", "Round Robin schedules processes using a time quantum.");
        add("OS", "Memory Management", "What is virtual memory?", "A CPU cache", "A technique that uses secondary storage to extend apparent memory", "A file system", "A scheduling policy", "B", "Virtual memory lets programs use an address space larger than physical RAM.");
        add("OS", "Concurrency", "What does a mutex primarily provide?", "Mutual exclusion", "File compression", "Page replacement", "Process creation", "A", "A mutex protects a critical section so only one thread enters at a time.");
        add("OS", "File Systems", "Which operation creates a new directory in Unix-like systems?", "mkdir", "mkfile", "newdir", "createdir", "A", "mkdir creates a directory.");
        add("OS", "Deadlocks", "Which condition is one of the four necessary conditions for deadlock?", "Circular wait", "Infinite CPU", "Cache hit", "Paging", "A", "Circular wait is one of the four Coffman conditions.");
        add("OS", "Processes", "What is a thread?", "A lightweight unit of execution within a process", "A disk partition", "A file descriptor only", "A page table", "A", "A thread is an execution unit that shares resources within a process.");
        add("OS", "Memory Management", "What is a page fault?", "A CPU arithmetic error", "An access to a page not currently in physical memory", "A syntax error", "A network timeout", "B", "A page fault occurs when a referenced page is not resident in physical memory.");

        add("DSA", "Arrays", "What is the worst-case time complexity of linear search?", "O(1)", "O(log n)", "O(n)", "O(n log n)", "C", "Linear search may inspect every element, giving O(n) worst-case time.");
        add("DSA", "Linked Lists", "Which pointer commonly marks the first node of a linked list?", "tail", "head", "root", "top", "B", "The head pointer references the first node.");
        add("DSA", "Stacks & Queues", "Which principle does a queue follow?", "LIFO", "FIFO", "Random access", "Priority only", "B", "A queue follows first-in, first-out order.");
        add("DSA", "Trees", "Which traversal visits left subtree, root, then right subtree?", "Preorder", "Inorder", "Postorder", "Level order", "B", "Inorder traversal visits left, root, and right.");
        add("DSA", "Graphs", "What is the basic unit representing a connection in a graph?", "Edge", "Stack", "Array index", "Heap", "A", "An edge connects two vertices in a graph.");
        add("DSA", "Sorting", "Which sorting algorithm is stable and has O(n log n) worst-case time?", "Merge Sort", "Quick Sort", "Selection Sort", "Bubble Sort only", "A", "Merge sort is stable and has O(n log n) worst-case time.");
        add("DSA", "Searching", "What prerequisite does binary search require for a standard array implementation?", "The data must be sorted", "The data must be unique", "The array must be circular", "The array must be reversed", "A", "Binary search relies on sorted ordering to discard half the search space.");
        add("DSA", "Dynamic Programming", "Which property often makes a problem suitable for dynamic programming?", "Overlapping subproblems", "No repeated states", "Only random choices", "No recursion", "A", "DP is especially useful when subproblems overlap and optimal substructure exists.");


    }

    private void add(String subject, String topic, String text, String a, String b, String c, String d,
                      String correct, String explanation) {
        Question q = new Question();
        q.setSubject(subject);
        q.setTopic(topic);
        q.setQuestionText(text);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectOption(correct);
        q.setExplanation(explanation);
        q.setDifficulty(0.0);
        q.setDiscrimination(1.0);
        questionRepository.save(q);
    }
}
