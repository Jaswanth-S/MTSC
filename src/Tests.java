import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class Tests {
    //This method is to test reservation for one seat
    @Test
    public void basicRequest() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 1");
        List<String> outputs = t.processRequests(requests);
        assertEquals("R001 F1", outputs.get(0));
    }
    //Test case to test zero seats for a reservation
    @Test
    public void requestZeroSeats() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 0");
        List<String> outputs = t.processRequests(requests);
        assertEquals("For request: R001. Please reserve at least 1 seat", outputs.get(0));
    }
    //Test case to test negative seats for a reservation
    @Test
    public void requestNegativeSeats() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 -1");
        List<String> outputs = t.processRequests(requests);
        assertEquals("For request: R001. Please check the request.", outputs.get(0));
    }
    //Test case to test theatre full or not
    @Test
    public void theaterFull() {
        MovieTheater t1 = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 1");
        List<String> outputs = t1.processRequests(requests);
        assertEquals("R001 F1", outputs.get(0));


        MovieTheater t2 = new MovieTheater();
        requests = new ArrayList<>();
        requests.add("R001 4");
        requests.add("R002 600");
        outputs = t2.processRequests(requests);
        assertEquals("R001 F1 F2 F3 F4", outputs.get(0));
        assertEquals("Sorry, not enough seats is available for this request: R002", outputs.get(1));
    }

    // Test to check seats are assigned consecutively or not
    @Test
    public void consecutiveSeats() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 10");
        List<String> outputs = t.processRequests(requests);
        assertEquals("R001 F1 F2 F3 F4 F5 F6 F7 F8 F9 F10", outputs.get(0));
    }

    //Test to check split party reservation
    @Test
    public void splitParty() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 4");
        requests.add("R002 22");
        List<String> outputs = t.processRequests(requests);
        assertEquals("R001 F1 F2 F3 F4", outputs.get(0));
        assertEquals("R002 F8 F9 F10 F11 F12 F13 F14 F15 F16 F17 F18 E6 E7 E8 E9 E10 E11 E12 E13 E14 E15 E16", outputs.get(1));
    }

    //Test to check update is done for each split
    @Test
    public void updateAndSplit() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 1");
        requests.add("R002 2");
        List<String> outputs = t.processRequests(requests);
        assertEquals("R001 F1", outputs.get(0));
        assertEquals("R002 F5 F6", outputs.get(1));
    }

    //Test to check reservation is closer to center
    @Test
    public void closerToCenter() {
        MovieTheater t = new MovieTheater();
        List<String> requests = new ArrayList<>();
        requests.add("R001 1");
        requests.add("R002 2");
        requests.add("R003 2");
        List<String> outputs = t.processRequests(requests);
        assertEquals("R001 F1", outputs.get(0));
        assertEquals("R002 F5 F6", outputs.get(1));
        assertEquals("R003 F10 F11", outputs.get(2));
    }
}
