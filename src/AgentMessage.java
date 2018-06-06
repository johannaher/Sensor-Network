/**
 * AgentMessage
 * A subclass to Message
 * This class represents the type of message called Agent Messages.
 * It moves the agent from node to node in the environment and
 * keeps track of the shortest rout to an event.
 *
 */

public class AgentMessage extends Message{
    public RoutingTable AMRoutingTable;
    public static int maxSteps = 50;

    /**
     * AgentMessage()
     * Default constructor
     * Requires the event that was created along with the agent, as well as the node that creates both.
     */
    public AgentMessage(int event, Node startNode){
        super(event, startNode);
        this.AMRoutingTable = startNode.getNodeRT();
    }

    /**
     * move()
     * A functioin that moves the agent one random step
     * If the agent has visited the node that is randomized,
     * it looks for another possible node to visit. If all the
     * nodes was visited, visit a random node anyway.
     *
     */
    public void move(Node newNode){
        path.push(newNode);
        AMRoutingTable.setEventPath(eventId, path);
        hasMoved = true;
    }
}
