import java.util.*;
import java.io.*;

public class lazy2 {
  public static void main(String[] args) throws Exception {
    Scanner in = new Scanner(new File("lazy.in"));
    int n = in.nextInt();
    int k = in.nextInt();
    Grass[] grass = new Grass[n];
    for (int i = 0; i < n; i++) {
      grass[i] = new Grass(in.nextInt(), in.nextInt());
    }
    in.close();

    Arrays.sort(grass);
    // this uses .compareTo if the objects are Comparable

    System.out.println( Arrays.toString(grass) );

    int rightIndex = 0;
    int leftIndex = 0;
    int windowTotal = grass[leftIndex].g;
    while (rightIndex + 1 < n &&
           grass[rightIndex+1].x - grass[leftIndex].x <= 2 * k) {
      rightIndex++;
      windowTotal += grass[rightIndex].g;
    }

    int result = windowTotal;
    while (rightIndex < n - 1) {   // n-1 is last index
      rightIndex++;
      windowTotal += grass[rightIndex].g;
      while (grass[rightIndex].x - grass[leftIndex].x > 2 * k) {
        windowTotal -= grass[leftIndex].g;
        leftIndex++;
      }
      result = Math.max(result, windowTotal);
    }

    PrintWriter out = new PrintWriter(new File("lazy.out"));
    System.out.println(result);
    out.println(result);
    out.close();
  }

  static class Grass implements Comparable<Grass> {
    int x, g;

    Grass(int gg, int xx) {
      x = xx;
      g = gg;
    }

    // this must return an int indicating how this grass compares to other grass
    // >0 value means "this" is greater than other
    // <0 means "this" < other
    // ==0 means "this" == other
    public int compareTo(Grass other) {
      if (this.x < other.x) return -1;
      if (this.x > other.x) return +1;
      return 0;

      // return this.x - other.x;   // also works w/o if statement
    }

    public String toString() {
      return "{x=" + x + ",g=" + g + "}";
    }
  }
}

/* ANALYSIS

4 3    <-- 4 grass patches, Bessie can go k dist in any direction
4 7    <-- 4 units of grass at x=7
10 15  <-- 10 units of grass at x=15
2 2    <-- 2 units at x=2
5 1    <-- 5 units at x=1


0  1  2  3  4  5  6  7             15   <-- x
   5  2              4             10   <-- grass
---B---------        total=7
------B---------     total=7
---------B---------  total=7
   ---------B--------- total=11
      ---------B--------- total=6
         ---------B--------- total=4

prefix sums could help us with this problem if we could store all possible positions in an array and make a prefix sum array with all those x's

x can get up to 1 million = ok

but there's another strategy we can use...sliding window

Algorithm when every position is represented with its own element:
1. start a window of results that starts at left side, stretching as far right as it can
loop through this window to get an "initial result" - e.g. sum of grass amounts
(1mil)

2. slide the window through remaining positions: (1mil)
     add one to the window position(s)
     remove any values "falling out" on left side
     add any values "coming in" on right side
total for part 2: 3 million (approx)

overall total: 4 million (maybe 3, maybe 5, not important)


what if there were still 100k patches, but x <= 1 bil

must use coordinate compression
let's have an array of n elements, sorted by position  




1  2  8  15   <-- x
5  2  4  10   <-- grass
----               <-- initial window = total = 7
   ----            <-- next window by moving left endpoint and pushing
                       right side out as far as possible
                       total = 6
      -            <-- next window by moving left endpoint; couldn't push right
                       total = 4
         --        <-- next window by moving left; nothing left to push right
                       total = 10

would there be reason to set left side to x=10? no - we might as well walk it even further to the next actual patch of grass; by moving further right, the right side of window might reach even further



1  2  8  15   <-- x
5  2  4  10   <-- grass
----             <--- init window; total = 7
   ----          <--- move the right end forward; bring in the left end until
                      we have a valid window again
         --      <--- move right end again; bring in left end until window valid

algorithm:
initial window...  (up to n indexes)
set left side to index 0; advance right side as far as possible while still being valid window

sliding...
move right side 1 index   (up to n indexes)
  for each slide, bring in left side as much as necessary to make window valid
    (left side index could become equal to right index)
    (up to n indexes for a SINGLE slide)


isn't the efficiency n slides * n left hand adjustments = n^2

NO!

the TOTAL, DISTRIBUTED # of left hand adjustments is only n

true total is n times


*/
