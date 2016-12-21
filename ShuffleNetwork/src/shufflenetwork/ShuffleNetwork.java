/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shufflenetwork;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Alex
 */

public class ShuffleNetwork {
    
    public class Switch {
        
        public int k; //level
        public int index; //order in chunk
        public Switch output0;
        public Switch output1;
        public ArrayList<Integer> processors;
        
        public Switch (int k, int i) {
            this.k = k;
            this.index = i;
            processors = new ArrayList<Integer>();
        }
        
        public void setOutput(Switch o0, Switch o1) {
            this.output0 = o0;
            this.output1 = o1;
        }
    }
 
    private int n;
    private int k;
    private Switch[][] network;
    
    public ShuffleNetwork(int n) {
        this.n = n;
        this.k = (int) (Math.log(n) / Math.log(2));
        network = new Switch[n/2][k];
        this.createNetwork();
    }
    
    private void createNetwork() {
        int chunk = n/2;
        for (int i = 0; i < n/2; i++)
            for (int j = 0; j < k; j++)
                network[i][j] = new Switch(i, j);
        
        for (int j = 0; j < k-1; j++) {
            for (int i = 0; i < chunk/2; i++)
                network[i][j].setOutput(network[i*2][j+1], network[i*2+1][j+1]);
            for (int i = chunk/2; i < n/2; i++)
                network[i][j].setOutput(network[i*2-chunk][j+1], 
                                        network[i*2-chunk+1][j+1]);
        }
    }
    
    private Switch getInputSwitch(int p) {
        int chunk = n/2;
        Switch sw = network[p%chunk][0];
        return sw;
    }
    
    public void shuffle(int p, int m) {
       
        StringBuffer sP, sM;
        sP = new StringBuffer("0");
        sM = new StringBuffer("0");
        int copyp = p, copym = m;
 
        int i = 0;
        while (i < k - 1) {
            
            sP.append("0");
            sM.append("0");
            i++;
        }
        
        i = sP.length()-1;
        while (p > 0) {
            if (p%2 == 0) 
                sP.setCharAt(i, '0');
                
            else
                sP.setCharAt(i, '1');
            p = p / 2;
            i--;
        }
        
        i = sM.length()-1;
        while (m > 0) {
            if (m%2 == 0)
                sM.setCharAt(i, '0');
            else
                sM.setCharAt(i, '1');
            m = m / 2;
            i--;
        }
        
        System.out.println("Processsor: " + sP + " Memory: " + sM);
        
        Switch sw = getInputSwitch(copyp);
        if (sw.processors.contains(copyp)) {
            System.out.println("Conflict:" + copyp);
            return;
        }
        else
            sw.processors.add(copyp);
        for (i = 0; i < k; i++) {
            if (sP.charAt(i) == sM.charAt(i)) {
                System.out.println("Switch on level " + sw.k + 
                        " index " + sw.index + " direct.");
                if (sP.charAt(i) == '0')
                    sw = sw.output0;
                else
                    sw = sw.output1;
                
                if (i < k-1) {
                    if (sw.processors.contains(p))
                        System.out.println("Conflict:" + copyp);
                    sw.processors.add(copyp);
                }
            }
            else {
                System.out.println("Switch on level " + sw.k + 
                        " index " + sw.index + " cross.");
                if (sP.charAt(i) == '0')
                    sw = sw.output1;
                else
                    sw = sw.output0;
                if (i < k-1) {
                    if (sw.processors.contains(copyp))
                        System.out.println("Conflict:" + copyp);
                    sw.processors.add(copyp);
                }
            }
        }
    }
        
    public static void main(String[] args) {
        
        Scanner scan = new Scanner(System.in);
        int n;
        double val;
        
        do {
            System.out.println("Introduceti n putere a lui 2: ");
            n = scan.nextInt();
            val = (Math.log(n) / Math.log(2));
        }while (Math.floor(val) != val);
        
        ShuffleNetwork sn = new ShuffleNetwork(n);
        ArrayList<Integer> seq = new ArrayList<Integer>();
        
        int no_pairs = scan.nextInt();
        for (int i = 0; i < no_pairs; i++) {
            seq.add(scan.nextInt());
            seq.add(scan.nextInt());
        }
        
        System.out.println(seq);
        
        for (int i = 0; i < no_pairs; i++) {
            int p = seq.get(2*i);
            int m = seq.get(2*i + 1);
            sn.shuffle(p, m);
        }
        
  /*      int p = 0, m = 0;
        while (true) {
            do {
                System.out.println("Introduceti p < " + n);
                p = scan.nextInt();
            }while (p >= n);
        
            do {
                System.out.println("Introduceti m < " + n);
                m = scan.nextInt();
            }while (m >= n);
            
           sn.shuffle(p, m);
        }
        */
    }
    
}
