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
    private int agentChance;

    public Node(Position position, int agentChance){
        this.nodePosition = position;
        this.neighbours = new Node[8];
        this.nodeRoutingTable = new RoutingTable();
        this.messageQueue = new LinkedList<>();
        this.isQueryNode = false;
        this.nodesEvents = new HashMap<>();
        this.agentChance = agentChance;
    }

    public void messageHandler(){
        //plocka ut och kolla första meddelandet i kön
        //Message messageType = messageQueue.peek();
        //

        Random random = new Random();
        //om query meddelande
        if(!messageQueue.isEmpty()) {
            if(!(messageQueue.peek().hasMoved)) {
                if (messageQueue.peek() instanceof QueryMessage) {
                    QueryMessage queryMessage = (QueryMessage) messageQueue.poll();
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
                            if (queryMessage.path.size() < queryMessage.maxSteps) {
                                queryMessage.move(nextNode);
                                nextNode.addMessageToQueue(queryMessage);
                            }
                        }
                    } else {
                        //annars skicka till random granne
                        Node nextNode = null;
                        while (nextNode == null) {
                            nextNode = neighbours[random.nextInt(neighbours.length)];
                        }

                        if (queryMessage.path.size() < queryMessage.maxSteps) {
                            queryMessage.move(nextNode);
                            nextNode.addMessageToQueue(queryMessage);
                        }
                    }
                }

                //om agent meddelande
                if (messageQueue.peek() instanceof AgentMessage) {
                    AgentMessage agentMessage = (AgentMessage) messageQueue.poll();
                    nodeRoutingTable.syncTables(agentMessage.AMRoutingTable);
                    //System.out.println("Syncing table between Node[" + this.nodePosition.getX() + ":" + nodePosition.getY() + "] and agent" + agentMessage.getEventId());
                    //jämför vägar
                    //kortare hittad?
                    //byt ut routingtabell
                    //noden har längre väg eller ingen väg?
                    //ge routingtabell
                    //radnom steg till nod som ej besökts
                    Node nextNode = null;
                    while (nextNode == null) {
                        nextNode = neighbours[random.nextInt(neighbours.length)];
                    }

                    if (agentMessage.path.size() < agentMessage.maxSteps) {
                        agentMessage.move(nextNode);
                        nextNode.addMessageToQueue(agentMessage);
                    }
                    //om alla grannar besökta, random nod
                }


                //om respons meddelnade
                if (messageQueue.peek() instanceof ResponseMessage) {
                    //om hemma
                    //print plats, tid skapad, event id \n
                    //annars skicka enligt stack
                    ResponseMessage responseMessage = (ResponseMessage) messageQueue.poll();
                    if (responseMessage.path.empty()) {
                        System.out.println("EVENT HITTAT:\n Event" + responseMessage.event.getEventId() + " started at position: "
                                + responseMessage.event.getEventPosition() + ", at time: "
                                + responseMessage.event.getTimestamp() + " \n");
                    } else {
                        responseMessage.getNextPosition().addMessageToQueue(responseMessage);
                    }
                }
            }else{
                messageQueue.peek().hasMoved = false;
            }
        }
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

        //System.out.println("Node[" + nodePosition.getX() +":"+ nodePosition.getY() + "] created a query");
    }

    private ResponseMessage createResponseMessage(Stack<Node> queryPath, Event event){
        ResponseMessage responseMessage = new ResponseMessage(queryPath, event);

        //System.out.println("Node[" + nodePosition.getX() +":"+ nodePosition.getY() + "] created a response");

        return responseMessage;
    }

    public void createAgentMessage(int eventId){
        AgentMessage agentMessage = new AgentMessage(eventId, this);
        messageQueue.addLast(agentMessage);

        //System.out.println("Node[" + nodePosition.getX() +":"+ nodePosition.getY() + "] created an agent");
    }

    public void setNeighbour(Node neighbourNode, Direction direction){
        if(neighbours[direction.ordinal()] == null) {
            neighbours[direction.ordinal()] = neighbourNode;
            neighbourNode.setNeighbour(this, direction.opposite());
        }
    }

    public Event createEvent(int eventId, int timestamp){
        Event event = new Event(eventId, timestamp, this);
        nodesEvents.put(eventId,event);

        Stack<Node> eventPath = new Stack<>();
        eventPath.push(this);
        nodeRoutingTable.setEventPath(eventId, eventPath);

        //System.out.println("Node[" + nodePosition.getX() +":"+ nodePosition.getY() + "] created event" + eventId);

        Random random = new Random();
        if(random.nextInt(agentChance) > 0){
            //lägg till agenten i nodens kö
            createAgentMessage(event.getEventId());
        }

        return event;
    }

    private void addMessageToQueue(Message message){
        this.messageQueue.addLast(message);
    }

    /**
     * getNodeRT()
     * A method that returns the nodes current Routing
     * table and its content
     *
     * @return
     */
    public RoutingTable getNodeRT(){
        return this.nodeRoutingTable;
    }
}
