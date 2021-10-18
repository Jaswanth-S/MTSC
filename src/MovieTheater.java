import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Movie Theater Class
 */
public class MovieTheater {
    private  int rows;
    private int columns;
    private int availableSeats;
    private int[] center;
    private int[][] theater;
    private int[] numSeatsRow; // Number of available seats per row.

    //constructor for the MovieTheater class
    public MovieTheater() {
        rows = 10;
        columns = 20;
        availableSeats = rows * columns;
        center = new int[]{rows / 2, columns / 2};
        theater = new int[rows][columns];
        for (int[] row: theater) {
            // 1 - indicates that a seat is empty. 0 - indicates a seat is filled.
            Arrays.fill(row, 1);
        }
        numSeatsRow = new int[rows];
        Arrays.fill(numSeatsRow, columns);
    }

    /**
     * Process the reservation requests. Prints the rejected requests(if any).
     * @return the processed requests
     * @param requests the list of requests
     */
    public List<String> processRequests(List<String> requests) {
        List<String> outputs = new ArrayList<>();
        for(String request: requests) {
            outputs.add(reserveSeats(request));
        }
        return outputs;
    }

    /**
     * Process a single reservation request.
     * @return string following the output format
     * @param reservation A single reservation request
     */
    private String reserveSeats(String reservation) {
        String output = null;
        String[] split = reservation.split(" ");
        int requestSeats = Integer.parseInt(split[1]);
        // Checking if seats are available or not for the request
        if (requestSeats > availableSeats) {
            return "Sorry, not enough seats is available for this request: " + split[0];
        } else if (requestSeats == 0) {
            return "For request: " + split[0] + ". Please reserve at least 1 seat";
        }
        else if(requestSeats < 0){
            return "For request: " + split[0] + ". Please check the request.";
            }
         else {
            output = split[0];
        }
        // assignments - contains the available (row,col,num_of_seats) for the requested number of seats.
        List<Integer[]> assignments = searchSeats(requestSeats);
        for (Integer[] assignment: assignments) {
            // update the assigned seats and their surrounding +-3 seats and +-1 row(col) as unavailable
            updateTheater(assignment[0], assignment[1], assignment[2], false);
            for (int i = 0; i < assignment[2]; i++) {
                int j = assignment[0] + 65;   // convert row no: to Alphabet
                char c = (char)j;
                output = output + " "  + c + (assignment[1] + 1 + i);  // format as required for the output. ex: B4, B5
            }
        }

        return output;
    }

    /**
     * Search for NUM number of seats. The search starts from the center row,
     * and recursively expanding to the upper and lower rows. For example, say the
     * theater has row A, B, C, D, and E, if we cannot find enough seats on row A, we will
     * search B and C next, if no seats are found we go search D and E.
     * If no consecutive seats are found, recursively split the party and search.
     * The priorities of searching are the following:
     *  1. distance from the center
     *  2. consecutiveness: sitting on the same row
     *
     * Output example:
     * [0, 0, 3] means reserved 3 seats starting from A1 (i.e. reserved A1, A2, and A3).
     * @return List of assigned seats
     * @param num number of requested seats
     */
    public List<Integer[]> searchSeats(int num) {
        List<Integer[]> assignedSeats = new ArrayList<>();
        int row = center[0], dist = 1;   // row is the middle row in the entire theatre. Dist is the distance from the center row.
        boolean upper = false, lower = false;  // whether to move to upper row or lower row. True - move to upper/lower, False - No
        while (row > -1 && row < rows) {
            int col = searchRow(row, num);   // get the col number (index) from which the seats are available.
            if (col > -1) {             // if seats are available
                assignedSeats.add(new Integer[]{row, col, num});
                break;             // break from loop, as we found the available seats.
            } else if (!lower){
                lower = true;
                row = center[0] - dist;  // Now next iteration will search on a lower row with dist distance from center row.
            } else if (!upper) {
                upper = true;
                row = center[0] + dist;  // Now next iteration will search on an upper row with dist distance from center row.
            } else {
                dist++;                  // increment distance from the center row as we couldn't any available in the current row.
                upper = false; lower = false;
            }
        }
        //recursive split
        if (assignedSeats.size() == 0) {    // if no seats are found by expanding from the center row.
            assignedSeats.addAll(searchSeats(num / 2));   // split the party into half and search again
            for(Integer[] a: assignedSeats) {
                updateTheater(a[0], a[1], a[2], true);    // update the assigned seats as unavailable to the future reservations
            }
            assignedSeats.addAll(searchSeats(num - (num / 2)));  // search and assign seats for the other half of the party.
        }

        return assignedSeats;
    }

    /**
     * Search a ROW for NUM of seats
     * @return the starting column
     */
    public int searchRow(int row, int num) {
        if (numSeatsRow[row] < num) {     // if available seats in the row are less than required, -1.
            return -1;
        }

        int left = 0, right = 0;          // left ------ right indicates the range of seats available.  right >= left
        while (left < columns && right < columns) {    // overflowing conditions
            if (right - left + 1 == num) {        // whether the no: of available seats range : (right - left + 1) is what we want
                return left;          // return the index of the col from which the seats are available.
            }
            if (theater[row][left] == 0) {   // if the seat is unavailable, increment left,right pointers.
                left++;
                right = left;
            } else if (theater[row][right] == 1) {   // if seat is available, increment just the right pointer
                right++;
            } else {               // else just move to next seat : increment left,right pointers
                right++;
                left = right;
            }
        }

        return -1;      // if no seat found, return -1.
    }

    /**
     * Update the available seats in the theater according to the public
     * safety guidelines, which is a buffer of 3 seats and/or one row.
     */
    private void updateTheater(int row, int column, int num, boolean safe) {
        if (safe) {
            for (int i = 0; i < num; i++) {
                theater[row][column + i] = 0;
                availableSeats--;
                numSeatsRow[row]--;
            }
        } else {
            for (int c = column - 3; c < column + num + 3; c++) {        // make +-3 cols from current range of available seats
                if (c > -1 && c < columns && theater[row][c] == 1) {     // as unavailable.
                    theater[row][c] = 0;
                    availableSeats--;
                    numSeatsRow[row]--;
                }
            }

            for (int c = column - 1; c < column + num + 1; c++) {      // make the upper and lower row's cols as unavailable to
                if (c > -1 && c < columns) {                           // for increasing customers safety.
                    if (row - 1 > -1 && theater[row - 1][c] == 1) {
                        theater[row - 1][c] = 0;
                        availableSeats--;
                        numSeatsRow[row - 1]--;
                    }
                    if (row + 1 < rows && theater[row + 1][c] == 1) {
                        theater[row + 1][c] = 0;
                        availableSeats--;
                        numSeatsRow[row + 1]--;
                    }
                }
            }
        }
    }

}

