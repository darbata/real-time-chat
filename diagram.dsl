   1  1
    1      1   workspace "Real Time Chat Application" "Learning project for Kubernetes and general cloud native application development" {
    1      1     !identifiers hierarchial
    2      2
    3      3     model {
    4      4       user = person "User"
    5      5
    6      6       app = softwareSystem "Web App" "React"
    7      7
    8      8       ss = softwareSystem "Real Time Chat Application" {
    9      9         chatServer = container "Chat Server" "WebSocket/Spring Boot"
   10     10         dispatcher = container "Dispatcher" "Spring Boot"
   11     11         api = container "API Gateway" "Spring Boot"
   12     12         broker = container "Message Broker" "RabbitMQ"
   13     13         kvStore = container "Key Value Store" "DynamoDB"
   14     14         rdb = container "Relational Database" "PostgreSQL"
   15     15       }
   16     16
   17     17       user -> app
   18     18       app -> ss.api
   19     19       app -> ss.chatServer
   20     20
   21     21       ss.chatServer -> ss.broker
   22     22       ss.broker -> ss.chatServer
   23     23       ss.dispatcher -> ss.broker
   24     24       ss.broker -> ss.dispatcher
   25     25       ss.dispatcher -> ss.kvStore
   26     26       ss.kvStore -> ss.dispatcher
   27     27       ss.dispatcher -> ss.rdb
   28     28       ss.rdb -> ss.dispatcher
   29     29       ss.api -> ss.rdb
   30     30       ss.rdb -> ss.api
   31     31     }
   32     32
   33     33     views {
   34     34       container system "Diagram" {
   35     35           include *
   36     36           include user
   37     37       }
   38     38     }
   39     39
   40     40     styles {
   41     41       element "Person" {
   42     42           shape Person
   43     43       }
   44     44
   45     45       element "Web App" {
   46     46         shape WebBrowser
   47     47       }
   48     48
   49     49       element "Relational Database" {
   50     50         shape Cylinder
   51     51       }
   52     52
   53     53       element "Message Broker" {
   54     54         shape Pipe
   55     55       }
   56     56
   57     57       element "Container" {
   58     58         shape RoundedBox
   59     59       }
   60     60
   61     61     }
   62     62   }
   63
   64
   65
   66
   67
   68
   69
   70
   71
 NORMAL   main  󰬀  diagram.dsl                                                                                           :   30  Top   1:4    20:20   workspace "Real Time Chat Application" "Learning project for Kubernetes and general cloud native application development" {
   1     !identifiers hierarchial
   2
   3     model {
   4       user = person "User"
   5
   6       app = softwareSystem "Web App" "React"
   7
   8       ss = softwareSystem "Real Time Chat Application" {
   9         chatServer = container "Chat Server" "WebSocket/Spring Boot"
  10         dispatcher = container "Dispatcher" "Spring Boot"
  11         api = container "API Gateway" "Spring Boot"
  12         broker = container "Message Broker" "RabbitMQ"
  13         kvStore = container "Key Value Store" "DynamoDB"
  14         rdb = container "Relational Database" "PostgreSQL"
  15       }
  16
  17       user -> app
  18       app -> ss.api
  19       app -> ss.chatServer
  20
  21       ss.chatServer -> ss.broker
  22       ss.broker -> ss.chatServer
  23       ss.dispatcher -> ss.broker
  24       ss.broker -> ss.dispatcher
  25       ss.dispatcher -> ss.kvStore
  26       ss.kvStore -> ss.dispatcher
  27       ss.dispatcher -> ss.rdb
  28       ss.rdb -> ss.dispatcher
  29       ss.api -> ss.rdb
  30       ss.rdb -> ss.api
  31     }
  32
  33     views {
  34       container system "Diagram" {
  35           include *
  36           include user
  37       }
  38     }
  39
  40     styles {
  41       element "Person" {
  42           shape Person
  43       }
  44
  45       element "Web App" {
  46         shape WebBrowser
  47       }
  48
  49       element "Relational Database" {
  50         shape Cylinder
  51       }
  52
  53       element "Message Broker" {
  54         shape Pipe
  55       }
  56
  57       element "Container" {
  58         shape RoundedBox
  59       }
  60
  61     }
  62   }









 NORMAL   main  󰬀  diagram.dsl                                                                                          yG   30  Top   1:1    20:14orkspace "Real Time Chat Application" "Learning project for Kubernetes and general cloud native application development" {
  !identifiers hierarchial

  model {
    user = person "User"

    app = softwareSystem "Web App" "React"

    ss = softwareSystem "Real Time Chat Application" {
      chatServer = container "Chat Server" "WebSocket/Spring Boot"
      dispatcher = container "Dispatcher" "Spring Boot"
      api = container "API Gateway" "Spring Boot"
      broker = container "Message Broker" "RabbitMQ"
      kvStore = container "Key Value Store" "DynamoDB"
      rdb = container "Relational Database" "PostgreSQL"
    }

    user -> app
    app -> ss.api
    app -> ss.chatServer

    ss.chatServer -> ss.broker
    ss.broker -> ss.chatServer
    ss.dispatcher -> ss.broker
    ss.broker -> ss.dispatcher
    ss.dispatcher -> ss.kvStore
    ss.kvStore -> ss.dispatcher
    ss.dispatcher -> ss.rdb
    ss.rdb -> ss.dispatcher
    ss.api -> ss.rdb
    ss.rdb -> ss.api
  }

  views {
    container system "Diagram" {
        include *
        include user
    }
  }

  styles {
    element "Person" {
        shape Person
    }

    element "Web App" {
      shape WebBrowser
    }

    element "Relational Database" {
      shape Cylinder
    }

    element "Message Broker" {
      shape Pipe
    }

    element "Container" {
      shape RoundedBox
    }

  }
}
