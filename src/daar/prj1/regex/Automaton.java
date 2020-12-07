package daar.prj1.regex;

import daar.prj1.IRegexMatcher;

import java.util.*;
import java.util.Map.Entry;

public class Automaton implements IRegexMatcher {
    public Node start;
    public List<Node> ends;

    public Automaton(Node start, List<Node> ends) {
        this.start = start;
        this.ends = ends;
    }

    @Override
    public boolean match(String text) {
        Node curState;
        if (this.start.isFinal) return true;

        for (int i = 0; i < text.length(); i++) {
            curState = start;
            for (int j = i; j < text.length(); j++) {
                int cur = (int) text.charAt(j);
                /*
                  If current state's labels don't contain current letter, we find if contain DOT, DOT could be represented any char
                  else we restart the processus.
                 */
                if (curState.neighbors.containsKey(cur)) {
                    curState = curState.neighbors.get(cur).get(0);
                } else if (curState.neighbors.containsKey(RegEx.DOT)) {
                    curState = curState.neighbors.get(RegEx.DOT).get(0);
                } else break;
                if (curState.isFinal) return true;
            }
        }
        return false;
    }


    /**
     * Connect two sub-automaton with 3 different operators
     */
    public static Automaton linkAutomaton(Automaton a, Automaton b, int operator) {
        if (a == null)
            return b;
        if (operator == RegEx.ALTERN) {
            Node start = new Node();
            List<Node> ends = new ArrayList<>();
            ends.add(new Node());
            ends.get(0).isFinal = true;
            start.addTransition(RegEx.EPSILON, a.start);
            start.addTransition(RegEx.EPSILON, b.start);
            for (Node end : a.ends) {
                end.addTransition(RegEx.EPSILON, ends.get(0));
                end.isFinal = false;
            }
            for (Node end : b.ends) {
                end.addTransition(RegEx.EPSILON, ends.get(0));
                end.isFinal = false;
            }
            return new Automaton(start, ends);
        } else if (operator == RegEx.CONCAT) {
            for (Node end : a.ends) {
                end.addTransition(RegEx.EPSILON, b.start);
                end.isFinal = false;
            }
            for (Node end : b.ends) end.isFinal = true;
            return new Automaton(a.start, b.ends);
        } else if (operator == RegEx.ETOILE) {
            Node start = new Node();
            List<Node> ends = new ArrayList<>();
            ends.add(new Node());
            ends.get(0).isFinal = true;
            start.addTransition(RegEx.EPSILON, a.start);
            start.addTransition(RegEx.EPSILON, ends.get(0));
            for (Node end : a.ends) {
                end.addTransition(RegEx.EPSILON, ends.get(0));
                end.addTransition(RegEx.EPSILON, a.start);
                end.isFinal = false;
            }
            return new Automaton(start, ends);
        }
        return null;
    }


    public static Automaton parse(RegExTree regTree) {
        Automaton res;
        if (regTree.subTrees.isEmpty()) {
            res = parseRecursion(regTree);
            res.ends.get(0).isFinal = true;
            return res;
        }
        return parseRecursion(regTree);
    }

    /**
     * Construct NFA by post order traversal
     */
    private static Automaton parseRecursion(RegExTree regTree) {
        if (regTree == null)
            return null;
        if (regTree.subTrees.isEmpty()) {
            Node node1 = new Node();
            Node node2 = new Node();
            node1.addTransition(regTree.root, node2);
            ArrayList<Node> ends = new ArrayList<>();
            ends.add(node2);
            return new Automaton(node1, ends);
        }
        RegExTree left = regTree.subTrees.get(0);
        RegExTree right = regTree.subTrees.size() == 2 ? regTree.subTrees.get(1) : null;
        return linkAutomaton(parseRecursion(left), parseRecursion(right), regTree.root);
    }


    public static String toString(Automaton root) {
        StringBuilder res = new StringBuilder();
        LinkedList<Node> queue = new LinkedList<>();
        HashSet<Integer> visited = new HashSet<>();
        queue.addLast(root.start);
        while (!queue.isEmpty()) {
            Node cur = queue.pollFirst();
            if (visited.contains(cur.id)) continue;
            visited.add(cur.id);
            res.append(cur.id).append("[");
            for (Integer key : cur.neighbors.keySet()) {
                res.append(RegExTree.rootToString(key)).append("(");
                List<Node> nexts = cur.neighbors.get(key);
                int size = nexts.size();
                for (int i = 0; i < size - 1; i++) {
                    res.append(nexts.get(i).id).append(nexts.get(i).isFinal ? "f," : ",");
                    queue.addLast(nexts.get(i));
                }
                res.append(nexts.get(size - 1).id).append(nexts.get(size - 1).isFinal ? "f" : "").append(")");
                queue.addLast(nexts.get(size - 1));
            }
            res.append("]\n");
        }
        return res.toString();
    }

    private static void getAllNodes(Automaton automaton, Set<Node> nodes, Set<Node> nodesNoEps) {
        LinkedList<Node> queue = new LinkedList<>();
        Set<Node> nodesLinkedByEps = new HashSet<>();
        queue.addLast(automaton.start);
        nodes.add(automaton.start);
        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            for (Integer key : cur.neighbors.keySet()) {
                for (Node next : cur.neighbors.get(key)) {
                    if (key == RegEx.EPSILON) nodesLinkedByEps.add(next);
                    if (nodes.contains(next)) continue;
                    nodes.add(next);
                    queue.addLast(next);
                }
            }
        }
        if (nodesNoEps != null) {
            for (Node node : nodes) if (!nodesLinkedByEps.contains(node)) nodesNoEps.add(node);
        }
    }

    public static Automaton transformNFAToDFA(Automaton NFA) {
        // Reset Node's accumulator while creating the new automaton
        Node.cpt = 0;

        Set<Node> nodes = new HashSet<>();
        Set<Node> nodesNoEps = new HashSet<>();
        getAllNodes(NFA, nodes, nodesNoEps);
        Map<Integer, Node> idToNodes = new HashMap<>();

        // Create the new node for every group
        for (Node head : nodesNoEps) {
            Node tmp = new Node();
            tmp.isFinal = head.isFinal;
            idToNodes.put(head.id, tmp);
        }

        /*
          For every group's head node, we use DFS to search the node that link orther group's head node by no epsilon path.
          In this process, we add the path between new nodes
          */

        for (Node head : nodesNoEps) {
            Deque<Node> st = new ArrayDeque<>();
            Set<Node> group = new HashSet<>();
            st.push(head);
            group.add(head);
            while (!st.isEmpty()) {
                Node cur = st.pop();
                for (Integer label : cur.neighbors.keySet()) {
                    for (Node next : cur.neighbors.get(label)) {
                        if (idToNodes.containsKey(next.id))
                            idToNodes.get(head.id).addTransition(label, idToNodes.get(next.id));

                        if (group.contains(next)) continue;
                        if (label == RegEx.EPSILON) {
                            group.add(next);
                            st.push(next);
                            if (next.isFinal) idToNodes.get(head.id).isFinal = true;
                        }
                    }
                }
            }
        }
        // Once we have linked all new nodes, we create deterministic finite automaton by head and final nodes.
        List<Node> newEnds = new ArrayList<>();
        for (Entry<Integer, Node> pair : idToNodes.entrySet()) {
            if (pair.getValue().isFinal) newEnds.add(pair.getValue());
        }
        return new Automaton(idToNodes.get(NFA.start.id), newEnds);
    }

    private static boolean areTwoStatesEqual(List<List<Integer>> idToGroup, Node node1, Node node2) {
        for (Integer label : node1.neighbors.keySet()) {
            if (!node2.neighbors.containsKey(label)) return false;
            for (Node next1 : node1.neighbors.get(label)) {
                for (Node next2 : node2.neighbors.get(label)) {
                    if (idToGroup.get(next1.id) != idToGroup.get(next2.id)) return false;
                }
            }
        }
        for (Integer label : node2.neighbors.keySet()) {
            if (!node1.neighbors.containsKey(label)) return false;
            for (Node next2 : node2.neighbors.get(label)) {
                for (Node next1 : node1.neighbors.get(label)) {
                    if (idToGroup.get(next2.id) != idToGroup.get(next1.id)) return false;
                }
            }
        }
        return true;
    }

    private static void classifyNode(List<Node> idToNodes, List<List<Integer>> idToGroup, List<List<Integer>> statesGroup) {
        List<List<Integer>> newStatesGroup = new LinkedList<>();
        List<List<Integer>> newIdToGroup = new ArrayList<>(idToGroup);
        int numGroup;
        int newNumGroups;
        do {
            for (List<Integer> group : statesGroup) {
                boolean[] hasGroup = new boolean[idToNodes.size()];
                for (int i = 0; i < group.size(); i++) {
                    if (hasGroup[group.get(i)]) continue;
                    List<Integer> newGroup = new ArrayList<>();
                    newGroup.add(group.get(i));
                    for (int j = i + 1; j < group.size(); j++) {
                        if (areTwoStatesEqual(idToGroup, idToNodes.get(group.get(i)), idToNodes.get(group.get(j)))) {
                            newGroup.add(group.get(j));
                            newIdToGroup.set(group.get(j), newGroup);
                            hasGroup[group.get(j)] = true;
                        }
                    }
                    newStatesGroup.add(newGroup);
                    newIdToGroup.set(group.get(i), newGroup);
                }
            }
            numGroup = statesGroup.size();
            newNumGroups = newStatesGroup.size();
            statesGroup.clear();
            statesGroup.addAll(newStatesGroup);
            idToGroup.clear();
            idToGroup.addAll(newIdToGroup);
            newStatesGroup = new LinkedList<>();
        } while (numGroup != newNumGroups);
    }

    public static Automaton minimize(Automaton dfa) {
        // Reset Node's accumulator while creating the new automaton
        Node.cpt = 0;

        Set<Node> nodes = new HashSet<>();
        getAllNodes(dfa, nodes, null);
        List<Integer> statesAc = new ArrayList<>();
        List<Integer> statesNonAc = new ArrayList<>();

        // Two mapping list, we can find nodes by id, and find the group which the node belong
        List<Node> idToNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) idToNodes.add(null);
        List<List<Integer>> idToGroup = new ArrayList<>(nodes.size());
        for (int i = 0; i < nodes.size(); i++) idToGroup.add(null);

        // Separate accepting state and no-accepting state
        for (Node node : nodes) {
            idToNodes.set(node.id, node);
            if (node.isFinal) {
                statesAc.add(node.id);
                idToGroup.set(node.id, statesAc);
            } else {
                statesNonAc.add(node.id);
                idToGroup.set(node.id, statesNonAc);
            }
        }
        List<List<Integer>> statesNonAcGroup = new LinkedList<>();
        statesNonAcGroup.add(statesNonAc);
        List<List<Integer>> statesAcGroup = new LinkedList<>();
        statesAcGroup.add(statesAc);
        // Classify states until we can not separate every state group anymore
        classifyNode(idToNodes, idToGroup, statesNonAcGroup);
        classifyNode(idToNodes, idToGroup, statesAcGroup);

        // Updating the mapping list
        for (List<Integer> group : statesNonAcGroup) {
            Node node = new Node();
            for (int id : group) idToNodes.set(id, node);
        }
        List<Node> newEnds = new ArrayList<>();
        for (List<Integer> group : statesAcGroup) {
            Node node = new Node();
            node.isFinal = true;
            for (int id : group) idToNodes.set(id, node);
            newEnds.add(node);
        }

        // BFS to link paths between new states 
        Deque<Node> queue = new ArrayDeque<>();
        queue.addLast(dfa.start);
        boolean[] visited = new boolean[nodes.size()];
        while (!queue.isEmpty()) {
            Node cur = queue.pollFirst();
            Node newNode = idToNodes.get(cur.id);
            if (visited[newNode.id]) continue;
            visited[newNode.id] = true;
            for (Integer label : cur.neighbors.keySet()) {
                for (Node next : cur.neighbors.get(label)) {
                    newNode.addTransition(label, idToNodes.get(next.id));
                    queue.add(next);
                }
            }
        }

        return new Automaton(idToNodes.get(dfa.start.id), newEnds);
    }
}

class Node {
    public static int cpt = 0;
    public int id;
    public boolean isFinal;
    public Map<Integer, ArrayList<Node>> neighbors;

    public Node() {
        id = cpt++;
        neighbors = new HashMap<>();
    }

    public void addTransition(Integer label, Node node) {
        if (neighbors.containsKey(label)) {
            neighbors.get(label).add(node);
        } else {
            ArrayList<Node> nodes = new ArrayList<>();
            nodes.add(node);
            neighbors.put(label, nodes);
        }
    }
}
