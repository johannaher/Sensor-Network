import java.util.Stack;

public class ResponseMessage extends Message{
    Event event;

    /**
     * ResponseMessage()
     * Default constructor
     *
     * @param queryPath
     * @param event
     */
    public ResponseMessage(Stack<Node> queryPath, Event event){
        super(event.getEventId(), queryPath.peek());
        this.path = queryPath;
        this.event = event;
    }

    public Node getNextPosition(){
        hasMoved = true;
        return path.pop();
    }
}
