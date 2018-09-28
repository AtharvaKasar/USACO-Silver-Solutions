import java.util.*;
import java.io.*;

public class wormhole {
  public static void main(String[] args) throws Exception {
    Scanner in = new Scanner(new File("wormhole.in"));
    int n = in.nextInt();
    Wormhole[] ws = new Wormhole[n];
    for (int i = 0; i < n; i++) {
      ws[i] = new Wormhole(in.nextInt(), in.nextInt());
    }
    in.close();
    
    for (int i = 0; i < n; i++) {
      ws[i].setRight(ws);
    }
    
    int result = countCombos(ws, 0);
    
    PrintWriter out = new PrintWriter(new File("wormhole.out"));
    System.out.println(result);
    out.println(result);
    out.close();
  }
  
  // counts combos that lead to infinite loops, using
  //   the given wormhole array, starting by picking
  //   an unlinked wormhole to link up and trying its
  //   pairings
  static int countCombos(Wormhole[] ws, int i) {
    // advance to next unlinked wormhole
    while (i < ws.length && ws[i].linked != null) i++;
    
    // if none, count this if it has inf cycle
    if (i >= ws.length) {
      return hasInfCycle(ws) ? 1 : 0;
    }
    




    // count all combos for all linkages for wormhole i
    int result = 0;
    for (int j = i+1; j < ws.length; j++) {
      if (ws[j].linked != null) continue;
      
      // link this with j
      ws[i].link(ws[j]);
      // try all combinations with this linkage
      result += countCombos(ws, i+1);
      // unlink
      ws[i].unlink();
    }
    
    return result;
  }
  
  static boolean hasInfCycle(Wormhole[] ws) {
    for (int start = 0; start < ws.length; start++) {
      if (hasInfCycle(ws, ws[start])) return true;
    }
    return false;
  }

  static boolean hasInfCycle(Wormhole[] ws, Wormhole s) {
    // cow starts by going through wormhole s
    Wormhole cow = s.linked;
    while (cow != s) {
      if (cow.toRight == null) return false;
      
      // keep going through wormholes to the right
      cow = cow.toRight.linked;
    }
    return true;
  }
  
  static class Wormhole {
    Wormhole linked;
    Wormhole toRight;
    
    int x;
    int y;
    
    Wormhole(int x, int y) {
      this.x = x;
      this.y = y;
    }
    

    void link(Wormhole other) {
      this.linked = other;
      other.linked = this;
    }
    
    
    void unlink() {
      linked.linked = null;
      this.linked = null;
    }
    
    void setRight(Wormhole[] ws) {
      for (int i = 0; i < ws.length; i++) {
        if (ws[i] == this) continue;
        if (ws[i].y != this.y) continue;
        if (ws[i].x < this.x) continue;
        
        if (this.toRight == null ||
            ws[i].x < toRight.x) {
          toRight = ws[i];
        }
      }
    }
  }

}

/* ANALYSIS

up to 12 wormholes, even #, all paired together

ex: 6 wormhole
1-2 3-4 5-6
1-2 3-5 4-6
1-2 3-6 4-5
1-3 2-4 
1-3 2-5
1-3 2-6
1-4 2-3
1-4 2-5
1-4 2-6

1 could be paired with any of 5 other wormholes
next smallest unlinked wormhole could be paired with any of 3 remaining wormholes
next smallest unlinked wormhole only has 1 option left
combos (6 wormholes total): 5*3*1 = 15

combos (12 wormholes): 11*9*7*5*3*1 = 10,395 distinct sets of pairings

for each combination:
  try sending bessie into each wormhole
    if any give an infinite loop, then count this distinct pairing
  if bessie never got into an infinite loop, don't count this distinct pairing

*/
