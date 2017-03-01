import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;



/**
 *  Program  reads an undirected graph description from a file input.txt and computes a minimum spanning tree for it by using Boruvka's algorithm.
 *  Output is in format:
 *  For each main iteration components are printed with included nodes with their original names. When result is ready, it is printed by showing edges
 *  in minium spanning tree.
 */
public class MinSpanTreeBuruvka {
    
    //u and v are edges end nodes and w is weight of edge
    //First edge class is used in main algorithm.
    public static class Edge implements Comparable<Edge> {
        
        int u;
        int v;
        int w;
        
        public Edge(int u, int v, int w) {
            this.u = u;
            this.v = v;
            this.w = w;
        }
        //Edges are compared first by their weight, then by their u node and last by their
        //v node. 
        @Override
        public int compareTo(Edge e) {
            
            if(w == e.w) {
                if(u == e.u) {
                    return v-e.v;
                }
                else {
                    return u-e.u;
                }
                
            }
            return w - e.w;
        }
    }
    
    //Second edge class is used to get result to right order, since weight
    //there order is presented without weight having impact.
    public static class Edge2 implements Comparable<Edge2> {
        
        int u;
        int v;
        
        public Edge2(int u, int v) {
            this.u = u;
            this.v = v;
        }
        @Override
        public int compareTo(Edge2 e) {
            if (u == e.u) {
                return v-e.v;
            }
            else {
                return u - e.u;
            }
        }
    }
    
    
    //Node class represent nodes in graph. It contains Treeset that contains
    //possible edges for node. Nodes are compared by their id which is integer.
    public static class Node implements Comparable<Node> {
        int id;
        TreeSet<Edge> edges;
        
        public Node(int i) {
            id = i;
            edges = new TreeSet<>();
        }
        
        public void addEdge(Edge e) {
            edges.add(e);
        }

        @Override
        public int compareTo(Node o) {
            return id - o.id;
        }
    }
    
    //Component is part of graph and its contains some amount of nodes in TreeSet
    public static class Component implements Comparable<Component> {
       TreeSet<Node> nodes;
       int label;
       int cheapest = -1;
       
       public Component(int la) {
           label = la;
           nodes = new TreeSet<>();
       }
       
       public void addNode(Node n) {
           nodes.add(n);
       }
       @Override
       public int compareTo(Component c) {
           return label - c.label;
       }
    }
    
    public static class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node a, Node b) {
            return a.id - b.id;
        }
    }
    
    //Function that searches Component to find node with id x.
    //If node is found, it is returned, if not, return null
    public static Node searchNode(TreeSet<Component> f, int x) {
        
        for(Component c : f ) {
           for(Node n : c.nodes) {
              if(n.id == x) {
                 return n;
              }
           }
        }
        return null;
    }
    
    //Function that reads graph from input.txt file. Each line in file
    //contains one edge from graph in format "u v w" where all are non-negative
    //integers and and u and v contains edges end nodes and w edges weight.
    public static TreeSet<Component> readEdges() {
        
        TreeSet<Component> forest = new TreeSet<>();
        
        try {
           BufferedReader in = new BufferedReader(new FileReader("input.txt"));
           String line;
           String[] tmp;
           while((line = in.readLine()) != null) {
               
               tmp = line.split(" ");
               
               int u = Integer.parseInt(tmp[0]);
               int v = Integer.parseInt(tmp[1]);
               int w = Integer.parseInt(tmp[2]);
               
               Edge e = new Edge(u,v,w);
               Edge e2 = new Edge(v,u,w);
        
               //Each node for edge is searched from
               //treeset.
               Node node1 = searchNode(forest, u);
               Node node2 = searchNode(forest, v);
               
               //If node is not found, it is created and edge's added to it.
               //Also new component is created since at the beginning of algorithm
               //all nodes are in their own components. If nodes already exists,
               //edge is added to it. 
               if(node1 == null) {
                  node1 = new Node(u);
                  node1.addEdge(e);
                  Component c = new Component(node1.id);
                  c.addNode(node1);
                  forest.add(c);
               }
               else {
                  node1.edges.add(e);
               }
               
               if(node2 == null) {
                  node2 = new Node(v);
                  node2.addEdge(e2);
                  Component c = new Component(node2.id);
                  c.addNode(node2);
                  forest.add(c);
               }
               else {
                  node2.edges.add(e2);
               }
               
           }
           
        }
        catch(IOException e) {
	    System.out.println("input.txt not found");
	}        
        return forest;
    }
    
    //Union combines two components. New component gets name from bigger component
    //or if they are same size, from one with smaller integer as name.
    public static Component union(Component a, Component b) {
        
        if (a.nodes.size() > b.nodes.size()) {
            a.nodes.addAll(b.nodes);
            return a;
        }
        else if (a.nodes.size() < b.nodes.size()) {
            b.nodes.addAll(a.nodes);
            return b;
        }
        else {
            if (a.label < b.label) {
                a.nodes.addAll(b.nodes);
                return a;
            }
            else {
                b.nodes.addAll(a.nodes);
                return b;
            }
        }
    } 
    
    //Function finds cheapest edge leaving component to node outside component.
    public static Edge findCheapestEdge(Component c, TreeSet<Component> f) {
        
        //Two first for loops iterate each node and each nodes edges to 
        //find edges going out from component. Then we find node that mactches
        //edges end node to make sure its outside our starting component. If it
        //is edges weight is compared to smallest edge so far and if it is
        //smaller, we keep it. At the end edge with smallest weight is returned.
        Edge cheapest = null;
        int smallest = Integer.MAX_VALUE;
        for(Node n: c.nodes) {
            for(Edge e : n.edges) {
                 if(e.v != c.label) {
                     for(Component co : f) {
                         for(Node n2 : co.nodes) {
                             if(n2.id == e.v) {
                                 if(co.label != c.label) {
                                     if (e.w < smallest) {
                                         cheapest = e;
                                         smallest = cheapest.w;
                                     }
                                 }
                             }
                         }
                     }
                 }
            }
        }
        return cheapest;
    }
    
    //Function that prints out given forest aka TreeSet containing components made from nodes
    public static void printForest(TreeSet<Component> f) {
        Iterator itr = f.iterator();
        Iterator itr2;
        while(itr.hasNext()) {
            Component c =(Component)itr.next();
            System.out.print(c.label + ": ");
            itr2 = c.nodes.iterator();
            while(itr2.hasNext()) {
                Node n = (Node)itr2.next();
                System.out.print(n.id);
                if(itr2.hasNext()){
                    System.out.print(" ");
                }
            }
            if(itr.hasNext()) {
                System.out.println();
            }
        }
        System.out.println();
    }
    
    //Main algorithm that calculates minimium spanning tree using Burovksa algorithm.
    public static void burovkaMST(TreeSet<Component> f) {
        
        //MST is result set.
        TreeSet<Edge2> MST = new TreeSet<>();
        while(f.size() > 1) {
            
            //For each component in forest, cheapest outgoing edge is searched.
            ArrayList<Edge> cheapest = new ArrayList<>();
            for(Component c : f) {
                Edge e = findCheapestEdge(c, f);
                cheapest.add(e);
            }
            
            //Then for each edge, its nodes are components are searched from forest
            while(!cheapest.isEmpty()) {
                Edge e = cheapest.remove(0);
                Node n1 = null;
                Node n2 = null;
                Component c1 = null;
                Component c2 = null;
                //Finding component and node for first end of edge
                for(Component c: f) {
                    for(Node n : c.nodes) {
                        if (n.id == e.u) {
                            n1 = n;
                            c1 = c;
                        } 
                    }
                }
                //Finding component and node for second end of edge
                for(Component c: f) {
                    for(Node n : c.nodes ) {
                        if(n.id == e.v) {
                            n2 = n;
                            c2 = c;
                        }
                    }
                }
                //Found components are combined using union.
                if(c1 != null && c2 != null) {
                   if(c1.label != c2.label) {
                      f.remove(c1);
                      f.remove(c2);
                      Component c3 = union(c1,c2);
                      f.add(c3);
                      
                      int u1;
                      int v1;
                      
                      //Edge that was used is then added to result MST
                      //in format u < v.
                      if(e.u < e.v) {
                          u1 = e.u;
                          v1 = e.v;
                      }
                      else {
                          u1 = e.v;
                          v1 = e.u;
                      }
                      Edge2 e2 = new Edge2(u1,v1);
                      MST.add(e2);
                   }    
                }
            }
            //After each iteration components are printed with nodes with origal names in them.
            printForest(f);
        }
        //At the end of algorithm, minium spanning tree is printed out.
        Iterator i = MST.iterator();
        while(i.hasNext()){
            Edge2 e = (Edge2)i.next();
            System.out.print(e.u + "-" + e.v);
            if(i.hasNext()) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
    
    public static void main(String args[]) {
        
        TreeSet<Component> forest = readEdges();
        printForest(forest);
        burovkaMST(forest);
    }
  
}
