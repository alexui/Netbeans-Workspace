/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package benesnetwork;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author Alex
 */
public class BenesNetwork {
    
    public class Succ {
        
        public Succ(Node node, int c) {
            n = node;
            cost = c;
        }
        
        public Node n;
        public int cost; //00, 01, 10, 11
    }
        
    public abstract class Node {     
        
        public ArrayList<Succ> succs;
        public int k; //level
        public int index; //order in chunk
        public ArrayList<Integer> path;
        public ArrayList<Node> seq;
        
        public Node() {
            succs = new ArrayList<Succ>();
            path = new ArrayList<Integer>();
            seq = new ArrayList<Node>();
        }
        
        public boolean search(int p) {
            for (Succ s : succs) {
                for (int j =0; j < path.size(); j++)
                    s.n.path.add(path.get(j));
                s.n.path.add(s.cost);
                
                for (int j =0; j < seq.size(); j++)
                    s.n.seq.add(seq.get(j));
                s.n.seq.add(this);
            }
            this.reset();
            return false;
        }
        
        public void reset() {
            path.clear();
            seq.clear();
        }
        
        @Override
        public String toString(){
            return "["+index+" "+k+"]";
        }
    }

    public class Switch extends Node{
                
        public Switch (int i, int k) {
            super();
            this.k = k;
            this.index = i;
        }
              
    }
    
    public class EndLeaf extends Node {
        

        public EndLeaf (int i, int k) {
            super();
            this.k = k;
            this.index = i;
        }
        
        @Override
        public boolean search(int p) {
            if ( index == p ) {
                 this.showPath();
                 return true;
            }
            return false;
        }
        
        private void showPath() {
        //    System.out.println("salut am gasit");
            System.out.println(path);
            System.out.println(seq);
            
            for (int i = 1; i < path.size(); i++) {
                if (path.get(i-1)%2 == path.get(i)/2)
                    System.out.println(seq.get(i)+" direct");
                else
                    System.out.println(seq.get(i)+" invers");
            }
            
            for (int i = 1; i < path.size(); i++)
                System.out.print("***");
            
            System.out.println();
            
            this.reset();
        }
                
    }
    
    public class StartLeaf extends Node {
        
        public StartLeaf (int i, int k) {
            super();
            this.k = k;
            this.index = i;
        } 
    }
    
    private int n;
    private int k;
    private Switch[][] network;
    private StartLeaf[] startLeaves;
    private EndLeaf[] endLeaves;
    
    public BenesNetwork(int n) {
        this.n = n;
        this.k = 2 * ((int) (Math.log(n) / Math.log(2))) - 1;
        
        int val = 0;
        
    /*  System.out.println("N = " + n);
        System.out.println("K = " + k);
    */
        network = new Switch[n/2][k];
        startLeaves = new StartLeaf[n];
        endLeaves = new EndLeaf[n];
        
        for (int i = 0; i < n/2; i++) {
            for (int j = 0; j < k; j++)
                network[i][j] = new Switch(i, j);
            startLeaves[i] = new StartLeaf(i, -1);
            endLeaves[i] = new EndLeaf(i,-1);
            startLeaves[n/2+i] = new StartLeaf(n/2+i, -1);
            endLeaves[n/2+i] = new EndLeaf(n/2+i, -1);
        }
        
        this.createNetwork(0,n/2,0,k);
        this.appendLeaves();
    }
    
    private void createNetwork(int start_row, int end_row, int start_col, int end_col) {
        
        if (start_col + 1 == end_col)
            return;
        
        int i, j;
        int chunk = (end_row-start_row);
        
    //    System.out.println("start_col: " + start_col + " end_col: " + end_col + " start_row: " + start_row + " end_row: " + end_row);
        
        for (i = start_row; i < end_row; i++) {
            Succ succ1, succ2;
            succ1 = new Succ(network[start_row+(i-start_row)/2][start_col+1], i%2);
            succ2 = new Succ(network[chunk/2+start_row+(i-start_row)/2][start_col+1], 2+i%2);
            network[i][start_col].succs.add(succ1);
            network[i][start_col].succs.add(succ2);
            //se putea cu setOutput
        }
            
        
    //    System.out.println("chunk: " + chunk);
        for (i = start_row; i < start_row + chunk/2; i++){
            Succ succ1, succ2;
            succ1 = new Succ(network[start_row + 2*(i-start_row)][end_col-1], 0);
            succ2 = new Succ(network[start_row + 2*(i-start_row)+1][end_col-1], 2);
            network[i][end_col-2].succs.add(succ1);
            network[i][end_col-2].succs.add(succ2);
            
            succ1 = new Succ(network[start_row + 2*(i-start_row)][end_col-1], 1);
            succ2 = new Succ(network[start_row + 2*(i-start_row)+1][end_col-1], 3);
            network[i+chunk/2][end_col-2].succs.add(succ1);
            network[i+chunk/2][end_col-2].succs.add(succ2);
        }
        
        createNetwork(start_row, chunk/2, start_col+1, end_col-1);
        createNetwork(chunk/2, end_row, start_col+1, end_col-1);
    }
    
    private void appendLeaves() {
        
        int i;
        for (i = 0; i < n; i++) {
            Succ succ = new Succ(network[i/2][0], i%2);
            startLeaves[i].succs.add(succ);
            
            succ = new Succ(endLeaves[i], i%2 + 2);
            network[i/2][k-1].succs.add(succ);
        }
    }
    
    public void benes(int p, int m) {
        
        Stack<Node> stack = new Stack<Node>();       
        Node start = getInputNode(p);
        stack.push(start);
    //  System.out.println("Start: [" + start.index + " " + start.k + "] ");
        this.search(stack, m);
 
    }
    
    public void reset() {
        for (int i = 0; i < n/2; i++)
            for (int j = 0; j < k; j++)
                network[i][j].reset();
            
        for ( int i = 0; i < n; i++) {
            startLeaves[i].reset();
            endLeaves[i].reset();
        }
    }
        
    private void search(Stack<Node> s, int m) {
        
        if (s.isEmpty())
            return;
    /*    
        System.out.println("Stack: ");
        for (int k = 0; k < s.size(); k++)
            System.out.print("["+s.elementAt(k).index + " " + s.elementAt(k).k +"] ");
        System.out.println();
    */    
        Node n = s.pop();
        if(n.search(m));
            
                
        for (Succ succ : n.succs)
            s.add(succ.n);
        search(s, m);
    }
    
    private Node getInputNode(int p) {
        return startLeaves[p];
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        int n;
        double val;
        
        do {
            System.out.println("Introduceti n putere a lui 2: ");
            n = scan.nextInt();
            val = (Math.log(n) / Math.log(2));
        }while (Math.floor(val) != val);
        
        BenesNetwork sn = new BenesNetwork(n);
        ArrayList<Integer> seq = new ArrayList<Integer>();
        
    /*    for (int t = 0; t<sn.startLeaves[2].succs.size(); t++)
            System.out.println(sn.startLeaves[2].succs.get(t).n.index);
    */  
        
        System.out.println("Introduceti secventa:  ");
        int no_pairs = scan.nextInt();
        for (int i = 0; i < no_pairs; i++) {
            seq.add(scan.nextInt());
            seq.add(scan.nextInt());
        }
                
        for (int i = 0; i < no_pairs; i++) {
            int p = seq.get(2*i);
            int m = seq.get(2*i + 1);
            
            System.out.println("Procesor :"+p+" ,memorie: "+m);
            sn.benes(p, m);
            sn.reset();
        }
      
    }
    
}
