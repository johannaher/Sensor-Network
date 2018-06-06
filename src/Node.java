import java.util.*;

/**
 * Node
 * A class that represents the nodes in the environment.
 * A node has a position and a routing table. Each node
 * keeps track of its neighbours based on their position.
 * The node has a queue system for messages visiting.
 *
 */

public class Node {
    private Node[] neighbours;
    private Position nodePosition;
    private RoutingTable nodeRoutingTable;
    private LinkedList<Message> messageQueue;
    private boolean isQueryNode;
    private HashMap<Integer, Event> nodesEvents;
    private HashMap<QueryMessage, Integer> querysSent;
    private int querysAnswered;
    private float agentChance;
    /**
     * Node()
     * Constructor, requires a position and the chance to spawn an agent when spawning an event
     *
     * @param position
     * @param agentChance
     */
    public Node(Position position, float agentChance){
        this.nodePosition = position;
        this.neighbours = new Node[8];
        this.nodeRoutingTable = new RoutingTable();
        this.messageQueue = new LinkedList<>();
        this.isQueryNode = false;
        this.nodesEvents = new HashMap<>();
        this.querysSent = new HashMap<>();
        this.agentChance = agentChance;
        this.querysAnswered = 0;
    }

    /**
     * Update method to check querys and workqueue.
     */
    public void update(){
        if(isQueryNode){
            checkQueries();
        }
        messageHandler();
    }

    /**
     * Method used only for test
     * @return
     */
    public Position getNodePosition(){
        return nodePosition;
    }

    /**
     * Handler for work queue. Gets the first element in queue, and calls new handler depending on message type.
     */
    private void messageHandler(){
        //plocka ut och kolla första meddelandet i kön
        //Message messageType = messageQueue.peek();
        //

        if(!messageQueue.isEmpty()) {
            if(!(messageQueue.peek().hasMoved)) {
                if (messageQueue.peek() instanceof QueryMessage) {
                    messageHandler((QueryMessage) messageQueue.poll());
                }

                //om agent meddelande
                if (messageQueue.peek() instanceof AgentMessage) {
                    messageHandler((AgentMessage) messageQueue.poll());
                }

                //om respons meddelnade
                if (messageQueue.peek() instanceof ResponseMessage) {
                    //om hemma
                    //print plats, tid skapad, event id \n
                    //annars skicka enligt stack
                   messageHandler((ResponseMessage)messageQueue.poll());
                }
            }else{
                messageQueue.peek().hasMoved = false;
            }
        }
    }

    /**
     * Method used if node is a query node.
     * Checks every query that has been sent and if they should be resent or not.
     */
    private void checkQueries(){
        for (Map.Entry<QueryMessage, Integer> queryMessage: querysSent.entrySet()) {
            if(queryMessage.getValue() >= QueryMessage.maxSteps * 8 && !queryMessage.getKey().hasBeenResent()){
                QueryMessage newQuery = new QueryMessage(queryMessage.getKey().eventId, this);
                newQuery.resend();
                queryMessage.getKey().resend();
                messageQueue.addLast(newQuery);
            } else {
                queryMessage.setValue(queryMessage.getValue() + 1);
            }
        }
    }

    /**
     * Handler for query messages.
     * Checks if requested event is known.
     * If node created the event, discard query and create a response message to send back to query node.
     * If only knows about the event, send query in right direction.
     *
     * @param queryMessage to be handled
     */
    private void messageHandler(QueryMessage queryMessage){
        //System.out.println("nod[" + nodePosition.getX() + ":" + nodePosition.getY() + "] har query" + queryMessage.eventId + ", ålder:" + queryMessage.path.size());

        if (nodeRoutingTable.checkEvent(queryMessage.eventId)) {
            //skickas till nästa nod i nodens routingtabell för det eventet
            //kolla om noden vet något om eventet
            //har noden eventet?
            //System.out.println("noden har eventet i tabellen");

            //MÅSTE ÄNDRA TILL ATT KOLLA AVSTÅND ÄR 0 ISTÄLLET FÖR EVENTLISTAN!
            if (nodesEvents.containsKey(queryMessage.eventId)) {
                //event hittat
                //System.out.println("event hittat");
                //skicka tillbaks svar(skapa respons message)
                ResponseMessage responseMessage = createResponseMessage(queryMessage.path,
                        nodesEvents.get(queryMessage.eventId));
                responseMessage.getNextPosition().addMessageToQueue(responseMessage);
            } else {
                //System.out.println("väg till event hittat");

                //om noden har sig själv i pathen till event, ta bort sig själv ur pathen

                            /*
                            if(nodeRoutingTable.getEventPath(queryMessage.eventId).peek() == this){
                                nodeRoutingTable.getEventPath(queryMessage.eventId).pop();
                                nodeRoutingTable.setEventPath(queryMessage.getEventId(),
                                        nodeRoutingTable.getEventPath(queryMessage.eventId));
                            }*/

                Node nextNode = nodeRoutingTable.getEventPath(queryMessage.eventId).peek();
                if (queryMessage.path.size() < QueryMessage.maxSteps) {
                    queryMessage.move(nextNode);
                    nextNode.addMessageToQueue(queryMessage);
                }
            }
        } else {
            //annars skicka till random granne
            Node nextNode = getRandomNeighbour();

            if (queryMessage.path.size() < QueryMessage.maxSteps) {
                queryMessage.move(nextNode);
                nextNode.addMessageToQueue(queryMessage);
            }
        }
    }

    /**
     * Handler for agent message.
     * Syncs Routing tables with agent and node.
     * Checks what neighbours agent has visited and tries to send agent to an unvisited neighbour.
     * If all neighbours are visited, sends it to a rendom neighbour.
     * @param agentMessage
     */
    private void messageHandler(AgentMessage agentMessage) {
        nodeRoutingTable.syncTables(agentMessage.AMRoutingTable);

        Node nextNode = null;

        for(Direction direction : Direction.values()){
            if(!agentMessage.path.contains(neighbours[direction.ordinal()])){
                nextNode = neighbours[direction.ordinal()];
                break;
            }
        }

        if(nextNode == null){
            nextNode = getRandomNeighbour();
        }

        if (agentMessage.path.size() < AgentMessage.maxSteps) {
            agentMessage.move(nextNode);
            nextNode.addMessageToQueue(agentMessage);
        }

        //om alla grannar besökta, random nod
    }

    /**
     * Handler for response message
     * If response message has no more nodes to go to, it has come back to the node that sent it.
     * It will then print out the event information.
     * If there are nodes left in the stack, it is sent to the next node.
     * @param responseMessage
     */
    private void messageHandler(ResponseMessage responseMessage){
        if (responseMessage.path.empty()) {
            System.out.println("EVENT FOUND:\n Event" + responseMessage.event.getEventId() + " started at position: ["
                    + responseMessage.event.getEventPosition().nodePosition.getX() +","
                    + responseMessage.event.getEventPosition().nodePosition.getY()+ "], at timestep: "
                    + responseMessage.event.getTimestamp() + " \n");
            querysAnswered++;
        } else {
            responseMessage.getNextPosition().addMessageToQueue(responseMessage);
        }
    }

    /**
     * Method only used in test
     * @return
     */
    public Message peekInQueue(){
        return messageQueue.peek();
    }

    public void setQueryNode(){
        isQueryNode = !isQueryNode;
    }

    public boolean getQueryNode(){
        return isQueryNode;
    }

    public void createQueryMessage(int eventId){
        QueryMessage queryMessage = new QueryMessage(eventId, this);
        messageQueue.addLast(queryMessage);
        querysSent.put(queryMessage, 0);
    }

    /**
     * Creates a response message in response to an answered query message.
     * @param queryPath stack with nodes the query message visited to get here.
     * @param event event that was requested.
     * @return The response message.
     */
    private ResponseMessage createResponseMessage(Stack<Node> queryPath, Event event){

        return new ResponseMessage(queryPath, event);
    }

    /**
     * Creates an agent message.
     * Agent starts by knowing about the event that was created along with the agent.
     * @param eventId
     */
    private void createAgentMessage(int eventId){
        AgentMessage agentMessage = new AgentMessage(eventId, this);
        messageQueue.addLast(agentMessage);
    }

    /**
     * Sets neighbour nodes to the node.
     * Also sets itself as the neighbour to the neighbour node.
     * @param neighbourNode to be set as neighbour node
     * @param direction in what direction the neighbour is.
     */
    public void setNeighbour(Node neighbourNode, Direction direction){
        if(neighbours[direction.ordinal()] == null) {
            neighbours[direction.ordinal()] = neighbourNode;
            neighbourNode.setNeighbour(this, direction.opposite());
        }
    }

    /**
     * method only used for testing
     * @return
     */
    public Node[] getNeighbours(){
        return neighbours;
    }

    /**
     * Creates an event with the event id and timestamp given by the environment
     * @param eventId id to be set as event id
     * @param timestamp when the event was created
     * @return the created event.
     */
    public Event createEvent(int eventId, int timestamp){
        Event event = new Event(eventId, timestamp, this);
        nodesEvents.put(eventId,event);

        Stack<Node> eventPath = new Stack<>();
        eventPath.push(this);
        nodeRoutingTable.setEventPath(eventId, eventPath);

        Random random = new Random();

        if(random.nextFloat() < agentChance){
            createAgentMessage(event.getEventId());
        }

        return event;
    }

    /**
     * Adds a specific message last to the work queue.
     * Used as a way to "move" one message from one node to another.
     * @param message to be added to the queue.
     */
    private void addMessageToQueue(Message message){
        this.messageQueue.addLast(message);
    }

    /**
     * Returns a random neighbour
     * @return neighbour node.
     */
    private Node getRandomNeighbour(){
        Random random = new Random();
        Node nextNode = null;
        while (nextNode == null) {
            nextNode = neighbours[random.nextInt(neighbours.length)];
        }
        return nextNode;
    }

    /**
     * getNodeRT()
     * A method that returns the nodes current Routing
     * table and its content
     *
     * @return
     */
    public RoutingTable getNodeRT(){
        return nodeRoutingTable;
    }

    /**
     *  Calculates nodes succesrate by dividing succesful querys by total querys sent
     * @return succesrate as decimals
     */
    public float getSuccessRate(){
        if(querysSent.size() > 0){
            return (float)querysAnswered / querysSent.size();
        } else {
            return 0;
        }
    }

    /**
     * Method to get number of queries sent.
     * Useful information when determining the success rate of the node.
     * @return
     */
    public int getQueriesSent(){
        return querysSent.size();
    }

}
