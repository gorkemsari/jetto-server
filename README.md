jetto is open source library to develop chat apps, real-time games, social media apps etc. for mobile devices. 

jetto provides real-time bi-directional & secure communication. It creates TCP/IP based encrypted communication between server and clients.

Visit [jetto official website](http://www.jetto.org)

# Installation
Server **jetto** library can be added to project from gradle and maven by adding below codes.

## Gradle
```java
compile 'org.jetto:jetto-server:1.0.0'
```

## Maven
```java
<dependency>
    <groupId>org.jetto</groupId>
    <artifactId>jetto-server</artifactId>
    <version>1.0.0</version>
    <type>pom</type>
</dependency>
```

# Start Server
- Create a java application and add new java class

```java
/**
*
* @author gorkemsari
*/
public class ServerApp {
                                            
    public static void main(String[] args) {      

    }
}
```

- Add **ServerListener** interface to class
- Abstract methods should be added **onMessage, onStart, onStop, onError** automatically depends on your IDE
- If not added, abstract methods should be defined with **@Override** annotation manually

```java
import org.jetto.listener.ServerListener;

/**
*
* @author gorkemsari
*/
public class ServerApp implements ServerListener{
    
    public static void main(String[] args) {      
        
    }

    //message: client message
    //id: client id
    @Override
    public void onMessage(String message, String id) {
        //clients messages
    }

    //id: client id
    @Override
    public void onStart(String id) {
        //new client connected and secure communication is started
    }

    //id: client id
    @Override
    public void onStop(String id) {
        //client disconnected
    }

    //message: error message
    //id: client id
    @Override
    public void onError(String message, String id) {
        //some error occured while any client's request processed
    }
}
```

- Add new **ServerEndpoint** with defined port number
- Set listener to **ServerEndpoint** and start server
- Now server starts to wait connection requests from clients
- If any client connects to server, a random id is sent to client and new encryption key produced by **Diffie-Hellman** key exchange algorithm and secure communication is started between server and client. **Aes** encryption algorithm is used to encryption.

```java
import org.jetto.endpoint.ServerEndpoint;
import org.jetto.listener.ServerListener;

/**
*
* @author gorkemsari
*/
public class ServerApp implements ServerListener{

    static ServerEndpoint server;
    static int port = 2329;
    
    public static void main(String[] args) {      
        server = new ServerEndpoint(port);
        server.setServerListener(new ServerApp());
        server.start();
    }

    //message: client message
    //id: client id
    @Override
    public void onMessage(String message, String id) {
        //clients messages
        System.out.println("new message from " + id + ". message:" + message);
    }

    //id: client id
    @Override
    public void onStart(String id) {
        //new client connected and secure communication is started
        System.out.println("new client connected: " + id);
    }

    //id: client id
    @Override
    public void onStop(String id) {
        //client disconnected
        System.out.println("client disconnected: " + id);
    }

    //message: error message
    //id: client id
    @Override
    public void onError(String message, String id) {
        //some error occured while any client's request is processing
        System.out.println("error occured. message: " + message + "client: " + id);
    }
}
```

# Create Model
Models should be extended by **Model** class of **jetto**. **Model** class has **type** property for parsing processes.

```java
import org.jetto.model.Model;

/**
*
* @author gorkemsari
*/
public class MessageModel extends Model {
    private String header;
    private String message;

    /**
    * @return the header
    */
    public String getHeader() {
        return header;
    }

    /**
    * @param header the header to set
    */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
    * @return the message
    */
    public String getMessage() {
        return message;
    }

    /**
    * @param message the message to set
    */
    public void setMessage(String message) {
        this.message = message;
    }
}
```

# Parse to Json
As mentioned above, models are extended by **Model** class of **jetto**. While setting models properties also **type** property of **Model** should be set. Models are parsed to json by **toJson()** method of **Parser** class. **Parser** uses **gson** library on background for parsing process.

```java
import org.jetto.parser.Parser;

/**
*
* @author gorkemsari
*/
MessageModel messageModel = new MessageModel();
messageModel.setHeader("This is header");
messageModel.setMessage("Hello from server!");
messageModel.setType(1);//setType is extended from Model class of jetto

Parser parser = new Parser();//parser class of jetto
String json = parser.toJson(messageModel);
```

# Parse to Model
The message type can be retrieved by **getType()** method of **Parser** when a json message is received. Json messages are parsed to related model by **toModel()** method of **Parser** class according to type.

```java
import org.jetto.parser.Parser;

/**
*
* @author gorkemsari
*/
String json = "{
        "header": "This is header",
        "message": "Hello from server!",
        "type": "1"
    }";
Parser parser = new Parser();//parser class of jetto
int type = parser.getType(json);

switch (type) {
    case 1:  
        MessageModel messageModel = parser.toModel(json, MessageModel.class);
        ...
        break;
    case 2:  
        OtherModel otherModel = parser.toModel(json, OtherModel.class);
        ...
        break;
    default: 
        ...
        break;
}
```

# Send Message
**jetto** sends all messages as byte array on background. Any format of string or json can be selected on high level usage. All messages will be caught on the **onMessage** method of clients.


## Send String Message to Specific Client
id is produced by server at begining of connection process and sent to client to define it as unique.

```java
String id = "5345ud-9ur6j-dfg34-3e82gb";//client id
server.write("Hello from server!", id);
```

## Send String Message to Specific Clients
Define a client id list.

```java
List<String> idList = new ArrayList<>();
idList.add("5345ud-9ur6j-dfg34-3e82gb");//client id
idList.add("e4t56-l9k2nb-0iplm-441e7y");//client id
idList.add("8yfd5s-33vbn6-27y5r-r5fxm");//client id

server.write("Hello from server!", idList);
```

## Send String Message to All Clients
```java
server.write("Hello from server!");
```

## Send Json Message to Specific Client
```java
String id = "5345ud-9ur6j-dfg34-3e82gb";//client id

MessageModel messageModel = new MessageModel();
messageModel.setHeader("This is header");
messageModel.setMessage("Hello from server!");
messageModel.setType(1);//setType is extended from Model class or jetto

Parser parser = new Parser();//parser class of jetto
server.write(parser.toJson(messageModel), id);
```

## Send Json Message to Specific Clients
```java
List<String> idList = new ArrayList<>();
idList.add("5345ud-9ur6j-dfg34-3e82gb");//client id
idList.add("e4t56-l9k2nb-0iplm-441e7y");//client id
idList.add("8yfd5s-33vbn6-27y5r-r5fxm");//client id

MessageModel messageModel = new MessageModel();
messageModel.setHeader("This is header");
messageModel.setMessage("Hello from server!");
messageModel.setType(1);//setType is extended from Model class or jetto

Parser parser = new Parser();//parser class of jetto
server.write(parser.toJson(messageModel), idList);
```

## Send Json Message to All Clients
```java
MessageModel messageModel = new MessageModel();
messageModel.setHeader("This is header");
messageModel.setMessage("Hello from server!");
messageModel.setType(1);//setType is extended from Model class or jetto

Parser parser = new Parser();//parser class of jetto
server.write(parser.toJson(messageModel));
```

# Receive Message
**jetto** receives all messages as byte array on background. All messages will be caught on the **onMessage** method of server with sender client's id.

```java
import org.jetto.parser.Parser;

/**
*
* @author gorkemsari
*/
@Override
public void onMessage(String message, String id) {

    Parser parser = new Parser();//parser class of jetto
    int type = parser.getType(message);

    switch (type) {
        case 1:  
            MessageModel messageModel = parser.toModel(message, MessageModel.class);
            ...
            break;
        case 2:  
            OtherModel otherModel = parser.toModel(message, OtherModel.class);
            ...
            break;
        default: 
            ...
            break;
    }
}
```
