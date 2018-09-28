import java.util.*;
import java.io.*;

public class haybales {
  static final String FILE_NAME = "haybales";

  public static void main(String[] args) throws FileNotFoundException {
    // Note: BufferedReader(FileReader) & BufferedWriter did not produce
    //   noticeably faster results on USACO servers compared to this solution.
    //   This solution's test cases 4-10 ran in about 2.3s.
    
    // INITIAL INPUT //
    Scanner in = new Scanner(new File(FILE_NAME + ".in"));
    int n = in.nextInt();  // # bales
    int q = in.nextInt();  // # queries
    
    TreeSet<Integer> xVals = new TreeSet<>();
    for (int i = 0; i < n; i++) {
      xVals.add(in.nextInt());
    }
    // note: leave input file open for queries later

    // CALCULATIONS //
    
//    Map<Integer, Integer> prefixCounts = new TreeMap<>();
    // Note: Can't use Map here (using polymorphism), because TreeMap-specific
    //   search methods are needed.
    
    TreeMap<Integer, Integer> prefixCounts = new TreeMap<>();
    // key: x-value of a haybale
    // value: count of haybales up to and INCLUDING this one, from left side
    
    int count = 0;
    // Note: Because elements are added in order, performance will be very
    //   sub-optimal, however it WILL be O(log n) since TreeMaps self-balance.
    for (int x : xVals) {
      count++;
      prefixCounts.put(x, count);
    }
    // Count of 0 at left extreme of possibilities means there's no need for
    //   special case for a query A-value to the left of leftmost bale - it
    //   will ALWAYS find an entry in the tree for any query endpoint.
    prefixCounts.put(-1, 0);
    
    // Extra research: to test speed differences, try printing only greatest #
    //   of bales out of all queries, instead of individual count for all. 
//    int most = 0;
    
    // OUTPUT //
    PrintWriter out = new PrintWriter(new File(FILE_NAME + ".out"));
    for (int i = 0; i < q; i++) {
      int a = in.nextInt();
      int b = in.nextInt();
      
      int result = prefixCounts.floorEntry(b).getValue()
                 - prefixCounts.lowerEntry(a).getValue();
      // upper endpoint uses *floor*, which INCLUDES haybale at that location
      //   if one exists, since upper endpoint is inclusive
      //   (if one doesn't exist, goes to next lowest haybale - don't want
      //    to include any bales to the right of the upper endpoint!)
      // lower endpoint uses *lower*, which looks LOWER than given value -
      //   does NOT include haybale at that location if one exists, since
      //   only the haybales OUTSIDE the range should be subtracted
      // both use Entry, which gets a small "bundling" object with both key
      //   and value for the pairing found - .floorKey etc could be used
      //   too, but then an extra O(log n) lookup would be necessary to get val
      
      // Extra research: does avoiding all these out.printlns improve speed?
//      most = Math.max(most, result);
      out.println(result);
      
      // avoid System.out repeated a large # of times - runtime nearly DOUBLES!
//      System.out.println(result);
    }
    
    // Extra research: Print only the biggest result from all queries, thereby
    //   doing the same amount of calculations (actually a bit more) but much
    //   less output. How much faster is this?
//    System.out.println(most);
//    out.println(most);
    
    in.close();
    out.close();
  }
}

/* ANALYSIS

Locations (x values) are too large to create an array for all. Therefore,
coordinates must be compressed. The simplest two options are:
* A Tree of x-values. .floor, .lower, .ceiling, and .higher can be used to
    locate nearest elements to a desired value in O(log n) time.
* A sorted array of x-values. Arrays.binSearch can be used to locate nearest
    elements in O(log n) time.
This solution will use a Tree.

A prefix sum structure will be helpful in answering queries quickly, without
looping through sections multiple times. Again, coordinates must be compressed.
Again, two choices:
* TreeMap, mapping the x-value of a bale (key) to the number of bales up to
    that point (value). Methods: .floorKey, .lowerKey, .ceilingKey, higherKey
                           ALSO same methods but with Entry, e.g. .floorEntry
* Array or parallel array of x-values paired with counts, sorted by x-values.
Again, this solution will use a TreeMap.

Algorithm:
For each bale [O(n) ~ 100k]...
  Add x-val to TreeSet [O(log n) ~ 17]

Keep a running count of bales, left to right.
For each bale [O(n) ~ 100k]...
  Add one to count [O(1)]
  Map x-val to count [O(log n) ~ 17]

For each query... [O(k) ~ 100k]
  Find count of haybales up to RIGHT endpoint [O(log n) ~ 17]
  Subtract count of haybales TO THE LEFT of LEFT endpoint [O(log n) ~ 17]
  Print difference [O(1)]

O(2n log n + 2k log n) = O((n+k)log n)
1.7 mil + 1.8 mil + 3.5 mil = 7 mil ... great!

Note: due to large amount of inputs / outputs, IO will actually dominate the
  running time much more than any calculations, assuming they are not grossly
  inefficient :(

*/
